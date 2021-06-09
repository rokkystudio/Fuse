package com.rokkystudio.fuse.diagram;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import android.content.Context;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.menu.NodeItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DiagramXml
{
    public static final String XML_ROOT = "root";
    public static final String XML_LOCATION = "location";
    public static final String XML_IMAGE = "image";
    public static final String XML_FUSE = "fuse";

    public static final String XML_ID = "id";
    public static final String XML_CURRENT = "current";
    public static final String XML_NAME = "name";
    public static final String XML_SRC = "src";

    public static NodeItem parse(@NonNull Context context, @NonNull String asset) {
        try {
            String path = context.getResources().getString(R.string.language) + "/" + asset;
            return parse(context.getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static NodeItem parse(@NonNull InputStream stream)
    {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new InputStreamReader(stream));
            return parse(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static NodeItem parse(@NonNull XmlPullParser parser) throws
            IOException, XmlPullParserException
    {
        NodeItem root = new NodeItem();
        NodeItem current = root;
        int eventType = parser.getEventType();

        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                NodeItem node = new NodeItem(name, link, parser.getName());

                if (XML_ROOT.equals(parser.getName())) {
                    String name = parser.getAttributeValue(null, XML_NAME);
                    current.getChilds()
                }

                else if (XML_MENU.equals(parser.getName())) {
                    current.addChild(node);
                }

                else if (XML_FOLDER.equals(parser.getName())) {
                    current.addChild(node);
                    current = node;
                }

                else if (XML_ITEM.equals(parser.getName())) {
                    current.addChild(node);
                }
            }

            if (eventType == END_TAG)
            {
                if (XML_FOLDER.equals(parser.getName())) {
                    current = current.getParent();
                }
            }

            eventType = parser.next();
        }
        return root;
    }
}
