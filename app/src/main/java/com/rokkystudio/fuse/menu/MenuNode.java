package com.rokkystudio.fuse.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuNode
{
    private String mName = null;
    private String mLink = null;
    private String mTag = null;

    private final List<MenuNode> mChilds = new ArrayList<>();

    private boolean mExpanded = false;
    private MenuNode mParent = null;

    public MenuNode() {}

    public MenuNode(String name) {
        mName = name;
    }

    public MenuNode(String name, String link) {
        mName = name; mLink = link;
    }

    public MenuNode(String name, String link, String tag) {
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

    public void setTagName(String tag) {
        mTag = tag;
    }

    public String getTagName() {
        return mTag;
    }

}
