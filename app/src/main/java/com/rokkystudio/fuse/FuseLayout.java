package com.rokkystudio.fuse;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FuseLayout extends ScrollView
{
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
        mRootLayout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        textView.setBackgroundColor(Color.BLUE);
        textView.setText(title);
        mRootLayout.addView(textView);
    }

    public void addLocation(String location) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        textView.setText(location);
        mRootLayout.addView(textView);
    }

    public void addFuse(String id, String current, String name)
    {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView idView = new TextView(getContext());
        idView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        idView.setTextColor(getResources().getColor(R.color.black));
        idView.setTextSize(getResources().getDimension(R.dimen.text_size));
        idView.setText(id);
        row.addView(idView);

        TextView currentView = new TextView(getContext());
        currentView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        currentView.setTextColor(getResources().getColor(R.color.black));
        currentView.setTextSize(getResources().getDimension(R.dimen.text_size));
        currentView.setText(current);
        row.addView(currentView);

        TextView nameView = new TextView(getContext());
        nameView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        nameView.setTextColor(getResources().getColor(R.color.black));
        nameView.setTextSize(getResources().getDimension(R.dimen.text_size));
        nameView.setText(name);
        row.addView(nameView);

        mRootLayout.addView(row);
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
        addTitle(parser.getAttributeValue("", "name"));
    }

    private void location(XmlPullParser parser) {
        addLocation(parser.getAttributeValue("", "name"));
    }

    private void fuse(XmlPullParser parser) {
        String id = parser.getAttributeValue("", "id");
        String current = parser.getAttributeValue("", "current");
        String name = parser.getAttributeValue("", "name");
        addFuse(id, current, name);
    }
}
