package com.rokkystudio.fuse.views.xml;

public class ObjectNode
{
    private final NodeType mType;

    public ObjectNode(NodeType type) {
        mType = type;
    }

    public NodeType getType() {
        return mType;
    }
}
