package com.rokkystudio.fuse.fuse;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import android.content.Context;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FuseXml
{
    public static final String XML_ROOT = "root";
    public static final String XML_LOCATION = "location";
    public static final String XML_IMAGE = "image";
    public static final String XML_FUSE = "fuse";

    public static final String XML_ID = "id";
    public static final String XML_CURRENT = "current";
    public static final String XML_NAME = "name";
    public static final String XML_SRC = "src";

    public static FuseItem parse(@NonNull Context context, @NonNull String asset) {
        try {
            String path = context.getResources().getString(R.string.language) + "/" + asset;
            return parse(context.getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static FuseItem parse(@NonNull InputStream stream)
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

    public static FuseItem parse(@NonNull XmlPullParser parser) throws
            IOException, XmlPullParserException
    {
        FuseItem root = new FuseItem();
        FuseItem current = root;
        int eventType = parser.getEventType();

        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                FuseItem item = new FuseItem();

                if (XML_ROOT.equals(parser.getName())) {
                    String name = parser.getAttributeValue(null, XML_NAME);
                    root.setName(name);
                    root.setTag(XML_ROOT);
                }
                else if (XML_LOCATION.equals(parser.getName())) {
                    String name = parser.getAttributeValue(null, XML_NAME);
                    root.setName(name);
                    current.addChild(item);
                    current = item;
                }

                else if (XML_IMAGE.equals(parser.getName())) {
                    String src = parser.getAttributeValue(null, XML_SRC);
                    item.setSrc(src);
                    current.addChild(item);
                }

                else if (XML_FUSE.equals(parser.getName())) {
                    String id = parser.getAttributeValue(null, XML_ID);
                    String amper = parser.getAttributeValue(null, XML_CURRENT);
                    String name = parser.getAttributeValue(null, XML_NAME);
                    item.setId(id);
                    item.setCurrent(amper);
                    item.setName(name);
                    current.addChild(item);
                }
            }

            if (eventType == END_TAG)
            {
                if (XML_LOCATION.equals(parser.getName())) {
                    current = current.getParent();
                }
            }

            eventType = parser.next();
        }
        return root;
    }
}
