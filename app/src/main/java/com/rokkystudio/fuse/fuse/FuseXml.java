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
        root.setTag(XML_ROOT);
        FuseItem current = root;
        int eventType = parser.getEventType();

        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                FuseItem item = new FuseItem();
                item.setTag(parser.getName());

                if (XML_ROOT.equals(parser.getName())) {
                    root.setName(parser.getAttributeValue(null, XML_NAME));
                }

                else if (XML_LOCATION.equals(parser.getName())) {
                    item.setName(parser.getAttributeValue(null, XML_NAME));
                    current.addChild(item);
                    current = item;
                }

                else if (XML_IMAGE.equals(parser.getName())) {
                    item.setSrc(parser.getAttributeValue(null, XML_SRC));
                    current.addChild(item);
                }

                else if (XML_FUSE.equals(parser.getName())) {
                    item.setId(parser.getAttributeValue(null, XML_ID));
                    item.setCurrent(parser.getAttributeValue(null, XML_CURRENT));
                    item.setName(parser.getAttributeValue(null, XML_NAME));
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
