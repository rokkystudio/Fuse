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

public class MenuLayout extends ScrollView implements NodeItem.OnNodeClickListener
{
    private final LinearLayout mRootLayout;
    private NodeItem mRootNode = null;

    private OnMenuClickListener mMenuClickListener = null;

    public MenuLayout(Context context) {
        super(context);
        mRootLayout = new LinearLayout(getContext());
        mRootLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mRootLayout);
    }

    public void setMenu(NodeItem menu)
    {
        if (mRootNode != null) {
            mRootNode.setOnNodeClickListener(null);
        }
        mRootNode = menu;
        mRootLayout.removeAllViews();
        if (menu != null) {
            mRootLayout.addView(menu.getView(getContext()));
            menu.setOnNodeClickListener(this);
        }
    }

    // TODO ADD LINE
    private void addLine() {
        // mCurrentLayout.addView(mLayoutInflater.inflate(R.layout.line, this, false));
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    @Override
    public void onNodeClick(NodeItem nodeItem)
    {
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
            if (nodeItem.isExpanded()) {
                nodeItem.getView(getContext()).collapse();
            } else {
                nodeItem.getView(getContext()).expand();
            }
        }
    }

    public interface OnMenuClickListener {
        void onMenuClick(String name, String link);
        void onItemClick(String name, String link);
    }
}
