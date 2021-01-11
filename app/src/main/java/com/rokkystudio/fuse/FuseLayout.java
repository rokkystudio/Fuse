package com.rokkystudio.fuse;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FuseLayout extends ScrollView
{
    private LayoutInflater mLayoutInflater;
    private LinearLayout mRootLayout;

    public FuseLayout(Context context) {
        super(context);
        init(context);
    }

    public FuseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        mRootLayout.setLayoutParams(params);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mRootLayout);
    }

    public void loadXml(int resource) {
        try {
            parseXML(getContext().getResources().getXml(resource));
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    public void loadXml(String name) {
        try {
            InputStream stream = getContext().getAssets().open(name);
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new InputStreamReader(stream));
            parseXML(parser);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    public void addTitle(String title) {
        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_title, mRootLayout, false);
        ((TextView) viewGroup.findViewById(R.id.FuseTitle)).setText(title);
        mRootLayout.addView(viewGroup);
    }

    public void addLocation(String location) {
        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_location, mRootLayout, false);
        ((TextView) viewGroup.findViewById(R.id.FuseLocation)).setText(location);
        mRootLayout.addView(viewGroup);
    }

    public void addFuse(String id, String type, String current, String name) {
        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_item, mRootLayout, false);
        ((TextView) viewGroup.findViewById(R.id.FuseID)).setText(id);
        ((ImageView) viewGroup.findViewById(R.id.FuseIcon)).setImageResource(getFuseImage(type, current));
        ((TextView) viewGroup.findViewById(R.id.FuseName)).setText(name);
        mRootLayout.addView(viewGroup);
    }

    public void addSeparator() {
        mRootLayout.addView(mLayoutInflater.inflate(R.layout.fuse_separator, mRootLayout, false));
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        int eventType = parser.getEventType();
        while (eventType != END_DOCUMENT)
        {
            if (eventType == START_TAG)
            {
                switch (parser.getName())
                {
                    case "fuses":
                        fuses(parser);
                        break;
                    case "location":
                        location(parser);
                        break;
                    case "fuse":
                        fuse(parser);
                        break;
                }
            }

            eventType = parser.next();
        }
    }

    private void fuses(XmlPullParser parser) {
        addTitle(parser.getAttributeValue(null, "name"));
    }

    private void location(XmlPullParser parser) {
        addLocation(parser.getAttributeValue(null, "name"));
    }

    private void fuse(XmlPullParser parser) {
        String id = parser.getAttributeValue(null, "id");
        String type = parser.getAttributeValue(null, "type");
        String current = parser.getAttributeValue(null, "current");
        String name = parser.getAttributeValue(null, "name");
        addFuse(id, type, current, name);
        addSeparator();
    }

    private int getFuseImage(String type, String current)
    {
        if (type.equalsIgnoreCase("MEDIUM"))
        {
            switch (current) {
                case "1A":
                    return R.drawable.medium_1a;
                case "2A":
                    return R.drawable.medium_2a;
                case "3A":
                    return R.drawable.medium_3a;
                case "5A":
                    return R.drawable.medium_5a;
                case "7.5A":
                    return R.drawable.medium_7_5a;
                case "10A":
                    return R.drawable.medium_10a;
                case "15A":
                    return R.drawable.medium_15a;
                case "20A":
                    return R.drawable.medium_20a;
                case "25A":
                    return R.drawable.medium_25a;
                case "30A":
                    return R.drawable.medium_30a;
                case "40A":
                    return R.drawable.medium_40a;
                case "50A":
                    return R.drawable.medium_50a;
            }
        }
        return 0;
    }
}
