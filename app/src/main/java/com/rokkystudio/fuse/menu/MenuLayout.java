package com.rokkystudio.fuse.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rokkystudio.fuse.views.CollapsedLayout;
import com.rokkystudio.fuse.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rokkystudio.fuse.menu.MenuXml.XML_FOLDER;
import static com.rokkystudio.fuse.menu.MenuXml.XML_ITEM;
import static com.rokkystudio.fuse.menu.MenuXml.XML_MENU;
import static com.rokkystudio.fuse.menu.MenuXml.XML_ROOT;

public class MenuLayout extends ScrollView implements View.OnClickListener
{
    private final LayoutInflater mLayoutInflater;
    private final LinearLayout mRootLayout;
    private ViewGroup mCurrentLayout;
    private MenuNode mRootNode = null;

    private OnMenuClickListener mMenuClickListener = null;

    public MenuLayout(Context context) {
        super(context);
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = new LinearLayout(getContext());
        LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        mRootLayout.setLayoutParams(params);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        mCurrentLayout = mRootLayout;
        addView(mRootLayout);
    }

    public void setMenu(MenuNode menu)
    {
        if (mRootNode != null) {
            mRootLayout.removeAllViews();
            mCurrentLayout = mRootLayout;
        }

        mRootNode = menu;
        folder(mRootNode);
    }

    private void separator() {
        mCurrentLayout.addView(mLayoutInflater.inflate(R.layout.separator, this, false));
    }

    private void folder(MenuNode node)
    {
        if (node == null) return;

        CollapsedLayout layout = (CollapsedLayout) mLayoutInflater.inflate(R.layout.menu_item, this, false);
        mCurrentLayout.addView(layout);
        layout.setOnClickListener(this);

        ((TextView) layout.findViewById(R.id.ItemName)).setText(node.getName());

        if (node.isRoot() || node.isExpanded())
        {
            ImageView menuIcon = layout.findViewById(R.id.ItemIcon);
            menuIcon.setImageResource(R.drawable.arrow_up);

            layout.setTag(node);
            layout.setExpanded(true);
            mCurrentLayout = layout.findViewById(R.id.WrapperLayout);

            if (node.isRoot() && !node.hasName()) {
                layout.findViewById(R.id.HeaderLayout).setVisibility(GONE);
                layout.findViewById(R.id.WrapperLayout).setBackground(null);
            }

            boolean first = true;

            for (MenuNode child : node.getChilds())
            {
                if (!first) separator();
                first = false;

                String tagName = child.getTagName();
                if (XML_ROOT.equals(tagName) || XML_FOLDER.equals(tagName)) {
                    folder(child);
                } else if (XML_MENU.equals(tagName) || XML_ITEM.equals(tagName)) {
                    item(child);
                }
            }
        } else {
            ImageView menuIcon = layout.findViewById(R.id.ItemIcon);
            menuIcon.setImageResource(R.drawable.arrow_down);
        }
    }

    private void item(MenuNode node) {
        ViewGroup layout = (ViewGroup) mLayoutInflater.inflate(R.layout.menu_item, this, false);
        mCurrentLayout.addView(layout);
        layout.setTag(node);
        layout.setOnClickListener(this);
        ((TextView) layout.findViewById(R.id.ItemName)).setText(node.getName());
        ImageView menuIcon = layout.findViewById(R.id.ItemIcon);
        menuIcon.setImageResource(R.drawable.arrow_right);
    }

    @Override
    public void onClick(View view)
    {
        MenuNode node = (MenuNode) view.getTag();
        if (node == null) return;

        if (XML_MENU.equals(node.getTagName())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick(node.getName(), node.getLink());
            }
            return;
        }

        if (XML_ITEM.equals(node.getTagName())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onItemClick(node.getName(), node.getLink());
            }
            return;
        }

        if (XML_FOLDER.equals(node.getTagName())) {
            CollapsedLayout layout = (CollapsedLayout) view;
            if (node.isExpanded()) {
                node.setExpanded(false);
                layout.collapse();
            } else {
                node.setExpanded(true);
                layout.expand();
            }
        }
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    public interface OnMenuClickListener {
        void onMenuClick(String name, String link);
        void onItemClick(String name, String link);
    }
}
