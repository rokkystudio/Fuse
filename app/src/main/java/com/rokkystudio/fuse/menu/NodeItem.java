package com.rokkystudio.fuse.menu;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class NodeItem
{
    private String mName = null;
    private String mLink = null;
    private String mTag = null;
    private NodeView mView = null;

    private final List<NodeItem> mChilds = new ArrayList<>();

    private boolean mExpanded = false;
    private NodeItem mParent = null;

    public NodeItem() {}

    public NodeItem(String name, String link, String tag) {
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

    public void setView(NodeView view) {
        mView = view;
    }

    public NodeView getView() {
        return mView;
    }

    public boolean hasChilds() {
        return !mChilds.isEmpty();
    }

    public List<NodeItem> getChilds() {
        return mChilds;
    }

    public void addChild(NodeItem child) {
        child.setParent(this);
        mChilds.add(child);
    }

    public void setParent(NodeItem parent) {
        mParent = parent;
    }

    public NodeItem getParent() {
        return mParent;
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public void expand() {
        mExpanded = true;
    }

    public void collapse() {
        mExpanded = false;
    }

    public boolean isExpanded() {
        return mExpanded;
    }
}
