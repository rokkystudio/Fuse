package com.rokkystudio.fuse.views.xml;

public class ParentNode extends ObjectNode
{
    public String mName;

    public ParentNode(String name) {
        super(NodeType.PARENT);
        mName = name;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
