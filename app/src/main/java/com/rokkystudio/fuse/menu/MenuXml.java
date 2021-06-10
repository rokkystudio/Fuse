package com.rokkystudio.fuse.menu;

import android.content.Context;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;

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
    // TODO Item date param

    public static final String XML_ROOT = "root";
    public static final String XML_MENU = "menu";
    public static final String XML_FOLDER = "folder";
    public static final String XML_ITEM = "item";

    public static final String XML_NAME = "name";
    public static final String XML_LINK = "link";

    public static MenuItem parse(@NonNull Context context, @NonNull String asset) {
        try {
            String path = context.getResources().getString(R.string.language) + "/" + asset;
            return parse(context.getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MenuItem parse(@NonNull InputStream stream)
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

    public static MenuItem parse(@NonNull XmlPullParser parser) throws
            IOException, XmlPullParserException
    {
        MenuItem root = new MenuItem();
        MenuItem current = root;
        int eventType = parser.getEventType();

        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                String name = parser.getAttributeValue(null, XML_NAME);
                String link = parser.getAttributeValue(null, XML_LINK);
                MenuItem node = new MenuItem(name, link, parser.getName());

                if (XML_ROOT.equals(parser.getName())) {
                    current.setName(name);
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
