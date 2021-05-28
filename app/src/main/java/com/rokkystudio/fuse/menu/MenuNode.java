package com.rokkystudio.fuse.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuNode
{
    private String mName;
    private String mLink;
    private List<MenuNode> mChilds = new ArrayList<>();
    private MenuNode mParent = null;
    private boolean mExpanded = false;

    public MenuNode() {}

    public MenuNode(String name) {
        mName = name;
    }

    public MenuNode(String name, String link) {
        mName = name; mLink = link;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getLink() {
        return mLink;
    }

    public boolean hasChilds() {
        return !mChilds.isEmpty();
    }

    public List<MenuNode> getChilds() {
        return mChilds;
    }

    public void addChild(MenuNode child) {
        child.setParent(this);
        mChilds.add(child);
    }

    public void setParent(MenuNode parent) {
        mParent = parent;
    }

    public MenuNode getParent() {
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
}
