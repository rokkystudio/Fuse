package com.rokkystudio.fuse.views.xml;

public class LocationNode extends ObjectNode
{
    public String mName;

    public LocationNode(String name) {
        super(NodeType.LOCATION);
        mName = name;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
