package com.rokkystudio.fuse.fuse;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.menu.MenuView;
import com.rokkystudio.fuse.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_FUSE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_IMAGE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_LOCATION;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_ROOT;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FuseLayout extends ScrollView implements View.OnClickListener
{
    private final LayoutInflater mLayoutInflater;
    private final LinearLayout mRootLayout;

    private ViewGroup mCurrentLocation = null;

    private OnImageClickListener mOnImageClickListener = null;

    public FuseLayout(Context context) {
        super(context);
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = new LinearLayout(context);
        mRootLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mRootLayout);
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
        ImageView image = new ImageView(getContext());
        image.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        if (getContext() == null) return;
        AssetManager assetManager = getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open(filename);
            Drawable drawable = BitmapDrawable.createFromStream(inputStream, null);
            image.setImageDrawable(drawable);
            image.setOnClickListener(this);
            image.setTag(filename);

            if (mCurrentLocation != null) {
                mCurrentLocation.addView(image);
            } else {
                mRootLayout.addView(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFuse(String id, String current, String name)
    {
        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_item, mRootLayout, false);
        ((TextView) viewGroup.findViewById(R.id.FuseID)).setText(id);
        ((ImageView) viewGroup.findViewById(R.id.FuseIcon)).setImageResource(getFuseImageId(current));
        ((TextView) viewGroup.findViewById(R.id.FuseName)).setText(name);
        viewGroup.setBackgroundColor(getBackgroundColor(current));

        if (mCurrentLocation != null) {
            mCurrentLocation.addView(viewGroup);
        } else {
            mRootLayout.addView(viewGroup);
        }
    }

    public void addSeparator() {
        View view = mLayoutInflater.inflate(R.layout.line, mRootLayout, false);

        if (mCurrentLocation != null) {
            mCurrentLocation.addView(view);
        } else {
            mRootLayout.addView(view);
        }
    }

    public void setFuseData(FuseItem fuseData)
    {
        // Root Item
        addTitle(fuseData.getName());
        for (FuseItem fuseItem : fuseData.getChilds())
        {
            switch (fuseItem.getTag())
            {
                case XML_LOCATION:
                    addLocation(fuseItem.getName());
                    break;
                case XML_IMAGE:
                    addImage(fuseItem.getSrc());
                    break;
                case XML_FUSE:
                    addFuse(fuseItem.getId(), fuseItem.getCurrent(), fuseItem.getName());
                    addSeparator();
                    break;
            }
        }
    }

    private int getBackgroundColor(String current)
    {
        switch (current) {
            case "1A":
                return getResources().getColor(R.color.bg_1a);
            case "2A":
                return getResources().getColor(R.color.bg_2a);
            case "3A":
                return getResources().getColor(R.color.bg_3a);
            case "5A":
                return getResources().getColor(R.color.bg_5a);
            case "7.5A":
                return getResources().getColor(R.color.bg_7_5a);
            case "10A":
                return getResources().getColor(R.color.bg_10a);
            case "15A":
                return getResources().getColor(R.color.bg_15a);
            case "20A":
                return getResources().getColor(R.color.bg_20a);
            case "25A":
                return getResources().getColor(R.color.bg_25a);
            case "30A":
                return getResources().getColor(R.color.bg_30a);
            case "40A":
                return getResources().getColor(R.color.bg_40a);
            case "50A":
                return getResources().getColor(R.color.bg_50a);
            case "60A":
                return getResources().getColor(R.color.bg_60a);
            case "70A":
                return getResources().getColor(R.color.bg_70a);
        }
        return Color.WHITE;
    }

    private int getFuseImageId(String current)
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
            case "60A":
                return R.drawable.fuse_60a;
            case "70A":
                return R.drawable.fuse_70a;
        }
        return 0;
    }

    @Override
    public void onClick(View view)
    {
        if (view instanceof ImageView) {
            String filename = (String) view.getTag();
            mOnImageClickListener.OnImageClick(filename);
            return;
        }

        ViewParent parent = view.getParent();
        if (!(parent instanceof MenuView)) return;
        MenuView wrapper = (MenuView) parent;

        ImageView arrow = wrapper.findViewById(R.id.CollapseArrow);
        if (wrapper.getMenuItem().isExpanded()) {
            arrow.setImageResource(R.drawable.arrow_down);
            wrapper.collapse();
        } else {
            arrow.setImageResource(R.drawable.arrow_up);
            wrapper.expand();
        }
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    public interface OnImageClickListener {
        void OnImageClick(String filename);
    }
}