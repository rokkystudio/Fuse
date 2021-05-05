package com.rokkystudio.fuse.views.xml;

public class ImageNode extends ObjectNode
{
    private String mSrc;
    private String mName;

    public ImageNode(String name, String src) {
        super(NodeType.IMAGE);
        mName = name; mSrc = src;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setImage(String src) {
        mSrc = src;
    }

    public String getImage() {
        return mSrc;
    }

}
