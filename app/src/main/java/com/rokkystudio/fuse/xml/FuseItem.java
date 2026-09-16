package com.rokkystudio.fuse.xml;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FuseItem
{
    private String mId;
    private String mCurrent;
    private String mName;
    private String mTag;
    private String mSrc;

    private FuseItem mParent = null;

    private final List<FuseItem> mChilds = new ArrayList<>();

    public FuseItem() {}

    public FuseItem(String name) {
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

    public void setParent(@NonNull FuseItem parent) {
        mParent = parent;
    }

    public FuseItem getParent() {
        return mParent;
    }

    public void addChild(@NonNull FuseItem item) {
        item.setParent(this);
        mChilds.add(item);
    }

    public List<FuseItem> getChilds() {
        return mChilds;
    }

    public boolean hasChilds() {
        return !mChilds.isEmpty();
    }

    public boolean isRoot() {
        return mParent != null;
    }
}
