package com.rokkystudio.fuse.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;

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

public class FuseLayout extends ScrollView implements
    View.OnClickListener, DiagramView.OnDiagramClickListener
{
    private LayoutInflater mLayoutInflater;
    private LinearLayout mRootLayout;

    private ViewGroup mCurrentLocation = null;

    private DiagramView.OnDiagramClickListener mOnDiagramClickListener = null;

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

    public void loadXml(String name) {
        try {
            InputStream stream = getContext().getAssets().open(getResources().getString(R.string.language) + "/" + name);
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
        mCurrentLocation = viewGroup;
        viewGroup.findViewById(R.id.LocationHeader).setOnClickListener(this);
    }

    public void addImage(String filename)
    {
        DiagramView diagram = new DiagramView(getContext());
        diagram.setFixed(true);
        diagram.setImageFromAsset(filename);
        diagram.setOnDiagramClickListener(this);

        if (mCurrentLocation != null) {
            mCurrentLocation.addView(diagram);
        } else {
            mRootLayout.addView(diagram);
        }
    }

    public void addFuse(String id, String current, String name)
    {
        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_item, mRootLayout, false);
        ((TextView) viewGroup.findViewById(R.id.FuseID)).setText(id);
        ((ImageView) viewGroup.findViewById(R.id.FuseIcon)).setImageResource(getFuseImage(current));
        ((TextView) viewGroup.findViewById(R.id.FuseName)).setText(name);

        if (mCurrentLocation != null) {
            mCurrentLocation.addView(viewGroup);
        } else {
            mRootLayout.addView(viewGroup);
        }
    }

    public void addSeparator() {
        View view = mLayoutInflater.inflate(R.layout.fuse_separator, mRootLayout, false);

        if (mCurrentLocation != null) {
            mCurrentLocation.addView(view);
        } else {
            mRootLayout.addView(view);
        }
    }

    private void parseXML(@NonNull XmlPullParser parser)
        throws XmlPullParserException, IOException
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
                    case "image":
                        image(parser);
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

    private void image(XmlPullParser parser) {
        addImage(parser.getAttributeValue(null, "src"));
    }

    private void fuse(XmlPullParser parser) {
        String id = parser.getAttributeValue(null, "id");
        String current = parser.getAttributeValue(null, "current");
        String name = parser.getAttributeValue(null, "name");
        addFuse(id, current, name);
        addSeparator();
    }

    private int getFuseImage(String current)
    {
        switch (current) {
            case "1A":
                return R.drawable.fuse_1a;
            case "2A":
                return R.drawable.fuse_2a;
            case "3A":
                return R.drawable.fuse_3a;
            case "5A":
                return R.drawable.fuse_5a;
            case "7.5A":
                return R.drawable.fuse_7_5a;
            case "10A":
                return R.drawable.fuse_10a;
            case "15A":
                return R.drawable.fuse_15a;
            case "20A":
                return R.drawable.fuse_20a;
            case "25A":
                return R.drawable.fuse_25a;
            case "30A":
                return R.drawable.fuse_30a;
            case "40A":
                return R.drawable.fuse_40a;
            case "50A":
                return R.drawable.fuse_50a;
        }
        return 0;
    }

    @Override
    public void onClick(View view)
    {
        ImageView arrow = view.findViewById(R.id.CollapseArrow);
        if (arrow.getTag() == null) arrow.setTag(false);

        ViewGroup wrapper = (ViewGroup) view.getParent();
        if (wrapper == null) return;
        if (wrapper.getId() != R.id.LocationWrapper) return;

        if ((boolean) arrow.getTag()) {
            // Развернуть
            arrow.setImageResource(R.drawable.arrow_up);
            arrow.setTag(false);
            expandLocation(wrapper);
        } else {
            // Свернуть
            arrow.setImageResource(R.drawable.arrow_down);
            arrow.setTag(true);
            collapseLocation(wrapper);
        }
    }

    private void expandLocation(ViewGroup wrapper)
    {
        int targetHeight = (int) wrapper.getTag();
        wrapper.getLayoutParams().height = wrapper.findViewById(R.id.LocationHeader).getHeight();
        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                wrapper.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(500);
        wrapper.startAnimation(animation);
    }

    private void collapseLocation(ViewGroup wrapper)
    {
        if (wrapper.getTag() == null) {
            wrapper.setTag(wrapper.getHeight());
        }

        int totalHeight = (int) wrapper.getTag();
        int targetHeight = wrapper.findViewById(R.id.LocationHeader).getHeight();

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                wrapper.getLayoutParams().height = interpolatedTime == 1 ? targetHeight : (int) (totalHeight - (totalHeight - targetHeight) * interpolatedTime);
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(500);
        wrapper.startAnimation(animation);
    }

    public void setOnDiagramClickListener(DiagramView.OnDiagramClickListener listener) {
        mOnDiagramClickListener = listener;
    }

    @Override
    public void onDiagramClick(String filename) {
        if (mOnDiagramClickListener != null) {
            mOnDiagramClickListener.onDiagramClick(filename);
        }
    }
}
