package com.rokkystudio.fuse.diagram;

import java.util.ArrayList;
import java.util.List;

public class DiagramNode
{
    private String mId;
    private String mCurrent;
    private String mName;
    private String mTag;
    private String mSrc;

    private DiagramNode mParent = null;

    private final List<DiagramNode> mChilds = new ArrayList<>();

    public DiagramNode(String name) {
        mName = name;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public void setSrc(String src) {
        mSrc = src;
    }

    public String getSrc() {
        return mSrc;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setCurrent(String current) {
        mCurrent = current;
    }

    public String getCurrent() {
        return mCurrent;
    }

    public void setParent(DiagramNode parent) {
        mParent = parent;
    }

    public DiagramNode getParent() {
        return mParent;
    }

    public boolean hasChilds() {
        return !mChilds.isEmpty();
    }

    public boolean isRoot() {
        return mParent != null;
    }
}
