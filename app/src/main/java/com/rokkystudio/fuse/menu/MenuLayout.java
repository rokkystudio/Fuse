package com.rokkystudio.fuse.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rokkystudio.fuse.menu.MenuXml.XML_FOLDER;
import static com.rokkystudio.fuse.menu.MenuXml.XML_ITEM;
import static com.rokkystudio.fuse.menu.MenuXml.XML_MENU;

public class MenuLayout extends ScrollView implements NodeView.OnHeaderClickListener
{
    private final LayoutInflater mLayoutInflater;
    private final LinearLayout mRootLayout;
    private ViewGroup mCurrentLayout;
    private NodeItem mRootNode = null;

    private OnMenuClickListener mMenuClickListener = null;

    public MenuLayout(Context context) {
        super(context);
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = new LinearLayout(getContext());
        mRootLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        mCurrentLayout = mRootLayout;
        addView(mRootLayout);
    }

    public void setMenu(NodeItem menu)
    {
        if (mRootNode != null) {
            mRootLayout.removeAllViews();
            mCurrentLayout = mRootLayout;
        }

        mRootNode = menu;
        attachNode(mRootNode);
    }

    private void addLine() {
        mCurrentLayout.addView(mLayoutInflater.inflate(R.layout.line, this, false));
    }

    private void attachNode(@NonNull NodeItem nodeItem)
    {
        NodeView nodeView = new NodeView(getContext());
        nodeView.setOnHeaderClickListener(this);
        nodeView.setNode(nodeItem);
        nodeItem.setView(nodeView);
        mCurrentLayout.addView(nodeView);

        ((TextView) nodeView.findViewById(R.id.ItemName)).setText(nodeItem.getName());
        ImageView menuIcon = nodeView.findViewById(R.id.ItemIcon);

        if (!nodeItem.hasChilds()) {
            menuIcon.setImageResource(R.drawable.arrow_right);
        } else if (nodeItem.isRoot() || nodeItem.isExpanded()) {
            menuIcon.setImageResource(R.drawable.arrow_up);
            nodeView.setExpanded(true);

            // Скрываем корневой элемент без имени
            if (nodeItem.isRoot() && !nodeItem.hasName()) {
                nodeView.findViewById(R.id.HeaderLayout).setVisibility(GONE);
                nodeView.findViewById(R.id.WrapperLayout).setBackground(null);
            }

            attachChilds(nodeItem);

        } else {
            menuIcon.setImageResource(R.drawable.arrow_down);
        }
    }

    public void attachChilds(NodeItem nodeItem)
    {
        NodeView nodeView = nodeItem.getView();
        if (nodeView == null) return;

        // Сохраняем текущий макет как родительский
        ViewGroup parentLayout = mCurrentLayout;
        // Меняем текущий макет для добавления дочерних элементов
        mCurrentLayout = nodeView.findViewById(R.id.WrapperLayout);

        boolean first = true;
        for (NodeItem child : nodeItem.getChilds())
        {
            if (!first) addLine();
            first = false;

            attachNode(child);
        }

        // Возвращаем назад предыдущий макет
        mCurrentLayout = parentLayout;
    }

    @Override
    public void onHeaderClick(@NonNull NodeView nodeView)
    {
        NodeItem nodeItem = nodeView.getNode();
        if (nodeItem == null) return;

        if (XML_MENU.equals(nodeItem.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick(nodeItem.getName(), nodeItem.getLink());
            }
            return;
        }

        if (XML_ITEM.equals(nodeItem.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onItemClick(nodeItem.getName(), nodeItem.getLink());
            }
            return;
        }

        if (XML_FOLDER.equals(nodeItem.getTag())) {
            if (nodeItem.isExpanded())
            {
                nodeItem.setExpanded(false);
                nodeView.removeAllViews();
            } else {
                expand(nodeItem);
            }
        }
    }

    public void expand(@NonNull NodeItem nodeItem) {
        nodeItem.setExpanded(true);
        NodeView nodeView = nodeItem.getView();
        if (nodeView == null) return;
        attachChilds(nodeItem);
        nodeView.expand();
    }

    /*
    private void expandToNode(@NonNull NodeItem node) {
        node.expand();
        if (node.getParent() != null) {
            expandToNode(node.getParent());
        }
    }

    private void collapseToNode(@NonNull NodeItem node) {
        node.collapse();
        for (NodeItem child : node.getChilds()) {
            collapseToNode(child);
        }
    }
    */

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    public interface OnMenuClickListener {
        void onMenuClick(String name, String link);
        void onItemClick(String name, String link);
    }
}
