package com.rokkystudio.fuse;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FuseTable extends TableLayout
{
    public FuseTable(Context context) {
        super(context);
    }

    public FuseTable(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public void addTitle(String title)
    {
        TableRow rowView = new TableRow(getContext());
        rowView.setLayoutParams(new LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowView.setBackgroundColor(Color.GRAY);

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        textView.setBackgroundColor(Color.BLUE);
        textView.setText(title);
        rowView.addView(textView);

        addView(rowView);
    }

    public void addLocation(String location)
    {
        TableRow rowView = new TableRow(getContext());
        rowView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        rowView.setBackgroundColor(Color.GREEN);

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        textView.setText(location);
        rowView.addView(textView);

        addView(rowView);
    }

    public void addFuse(String id, String current, String name)
    {
        TableRow rowView = new TableRow(getContext());
        rowView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        rowView.setBackgroundColor(Color.RED);

        TextView idView = new TextView(getContext());
        idView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        idView.setTextColor(getResources().getColor(R.color.black));
        idView.setTextSize(getResources().getDimension(R.dimen.text_size));
        idView.setText(id);
        rowView.addView(idView);

        TextView currentView = new TextView(getContext());
        currentView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        currentView.setTextColor(getResources().getColor(R.color.black));
        currentView.setTextSize(getResources().getDimension(R.dimen.text_size));
        currentView.setText(current);
        rowView.addView(currentView);

        TextView nameView = new TextView(getContext());
        nameView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        nameView.setTextColor(getResources().getColor(R.color.black));
        nameView.setTextSize(getResources().getDimension(R.dimen.text_size));
        nameView.setText(name);
        rowView.addView(nameView);

        addView(rowView);
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
