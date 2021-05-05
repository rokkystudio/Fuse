package com.rokkystudio.fuse.views.xml;

public class FuseNode extends ObjectNode
{
    private String mId;
    private String mCurrent;
    private String mName;

    public FuseNode(String id, String current, String name) {
        super(NodeType.FUSE);
        mId = id; mCurrent = current; mName = name;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setCurrent(String current) {
        mCurrent = current;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getCurrent() {
        return mCurrent;
    }

    public String getName() {
        return mName;
    }
}
