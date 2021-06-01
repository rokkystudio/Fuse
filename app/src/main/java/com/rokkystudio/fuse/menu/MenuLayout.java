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
        addNode(mRootNode);
    }

    private void addLine() {
        mCurrentLayout.addView(mLayoutInflater.inflate(R.layout.line, this, false));
    }

    private void addNode(NodeItem node)
    {
        if (node == null) return;

        NodeView nodeView = (NodeView) mLayoutInflater.inflate(R.layout.menu_item, this, false);
        nodeView.setOnHeaderClickListener(this);
        nodeView.setNode(node);
        node.setView(nodeView);
        mCurrentLayout.addView(nodeView);

        ((TextView) nodeView.findViewById(R.id.ItemName)).setText(node.getName());
        ImageView menuIcon = nodeView.findViewById(R.id.ItemIcon);

        if (!node.hasChilds()) {
            menuIcon.setImageResource(R.drawable.arrow_right);
        } else if (node.isRoot() || node.isExpanded()) {
            menuIcon.setImageResource(R.drawable.arrow_up);
            nodeView.setExpanded(true);

            // Скрываем корневой элемент без имени
            if (node.isRoot() && !node.hasName()) {
                nodeView.findViewById(R.id.HeaderLayout).setVisibility(GONE);
                nodeView.findViewById(R.id.WrapperLayout).setBackground(null);
            }

            attachChilds(node);

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
        // TODO NULL POINTER
        mCurrentLayout = nodeView.findViewById(R.id.WrapperLayout);

        boolean first = true;
        for (NodeItem child : nodeItem.getChilds())
        {
            if (!first) addLine();
            first = false;

            addNode(child);
        }

        // Возвращаем назад предыдущий макет
        mCurrentLayout = parentLayout;
    }

    @Override
    public void onHeaderClick(@NonNull NodeView layout)
    {
        NodeItem node = layout.getNode();
        if (node == null) return;

        if (XML_MENU.equals(node.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick(node.getName(), node.getLink());
            }
            return;
        }

        if (XML_ITEM.equals(node.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onItemClick(node.getName(), node.getLink());
            }
            return;
        }

        if (XML_FOLDER.equals(node.getTag())) {
            if (node.isExpanded())
            {
                node.collapse();
                NodeView nodeView = node.getView();
                if (nodeView != null) {
                    nodeView.removeAllViews();
                }
            } else {
                node.expand();
                attachChilds(node);
            }
        }
    }

    public void expand(@NonNull NodeItem nodeItem) {
        nodeItem.expand();
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
