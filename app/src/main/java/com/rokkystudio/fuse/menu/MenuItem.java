package com.rokkystudio.fuse.menu;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MenuItem
{
    private String mName = null;
    private String mLink = null;
    private String mTag = null;
    private MenuView mMenuView = null;

    private final List<MenuItem> mChilds = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    private boolean mExpanded = false;
    private MenuItem mParent = null;

    public MenuItem() {}

    public MenuItem(String name, String link, String tag) {
        mName = name; mLink = link; mTag = tag;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public boolean hasName() {
        return mName != null && !mName.isEmpty();
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getLink() {
        return mLink;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public void setView(MenuView view) {
        mMenuView = view;
    }

    public MenuView getView(Context context)
    {
        if (mMenuView != null) return mMenuView;
        mMenuView = new MenuView(context);
        mMenuView.setMenuItem(this);

        if (mExpanded) {
            for (MenuItem childItem : mChilds) {
                 MenuView childView = childItem.getView(context);
                mMenuView.addChildView(childView);
            }
        }
        return mMenuView;
    }

    public void removeViews()
    {
        if (mMenuView != null) {
            mMenuView.removeChildViews();
            mMenuView = null;
        }

        for (MenuItem child : mChilds) {
            child.removeViews();
        }
    }

    public boolean hasChilds() {
        return !mChilds.isEmpty();
    }

    public List<MenuItem> getChilds() {
        return mChilds;
    }

    public void addChild(MenuItem child) {
        child.setParent(this);
        mChilds.add(child);

        // TODO IF HAS VIEW THEN ADD VIEW
    }

    public void setParent(MenuItem parent) {
        mParent = parent;
    }

    public MenuItem getParent() {
        return mParent;
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;

        for (MenuItem child : mChilds) {
            child.setOnItemClickListener(listener);
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }
}
