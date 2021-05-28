package com.rokkystudio.fuse.menu;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class MenuXml
{
    private MenuNode mCurrentNode;
    private MenuNode mRootNode;

    public MenuNode parseStream(InputStream stream) {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new InputStreamReader(stream));
            parseXML(parser);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return mRootNode;
    }

    private void parseXML(@NonNull XmlPullParser parser)
            throws XmlPullParserException, IOException
    {
        mRootNode = new MenuNode();
        mCurrentNode = mRootNode;

        int eventType = parser.getEventType();
        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                switch (parser.getName())
                {
                    case "folder":
                        folder(parser);
                        break;
                    case "item":
                        item(parser);
                        break;
                }
            }

            if (eventType == END_TAG)
            {
                if ("folder".equals(parser.getName())) {
                    mCurrentNode = mCurrentNode.getParent();
                }
            }

            eventType = parser.next();
        }
    }

    private void folder(@NonNull XmlPullParser parser) {
        String name = parser.getAttributeValue(null, "name");
        String link = parser.getAttributeValue(null, "link");

        MenuNode node = new MenuNode(name, link);
        mCurrentNode.addChild(node);
        mCurrentNode = node;
    }

    private void item(@NonNull XmlPullParser parser) {
        String name = parser.getAttributeValue(null, "name");
        String link = parser.getAttributeValue(null, "link");

        MenuNode node = new MenuNode(name, link);
        mCurrentNode.addChild(node);
    }
}
