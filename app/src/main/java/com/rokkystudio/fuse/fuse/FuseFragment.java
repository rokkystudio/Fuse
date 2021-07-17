package com.rokkystudio.fuse.fuse;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_FUSE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_IMAGE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_LOCATION;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rokkystudio.fuse.CollapsedLayout;
import com.rokkystudio.fuse.R;

public class FuseFragment extends Fragment implements
    CollapsedLayout.OnHeaderClickListener
{
    private static final String FILENAME = "FILENAME";

    private LayoutInflater mLayoutInflater = null;

    private ViewGroup mRootView = null;
    private ViewGroup mMainView = null;
    private TextView mTitleView = null;
    private ViewGroup mCurrentLocation = null;

    @NonNull
    public static FuseFragment newInstance(String filename) {
        FuseFragment fragment = new FuseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILENAME, filename);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() == null) return;
        String path = getArguments().getString(FILENAME);

        FuseModel model = new ViewModelProvider(this).get(FuseModel.class);
        Context context = getContext();
        if (context != null) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            FuseItem data = FuseXml.parse(context, path);
            if (data != null) model.setFuseData(data);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fuse_fragment, container, false);
        mMainView = mRootView.findViewById(R.id.FuseMain);
        mTitleView = mRootView.findViewById(R.id.FuseTitle);

        FuseModel model = new ViewModelProvider(this).get(FuseModel.class);
        model.getFuseData().observe(getViewLifecycleOwner(), this::setData);
        model.getScrollPos().observe(getViewLifecycleOwner(), this::setScrollPos);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        FuseModel model = new ViewModelProvider(this).get(FuseModel.class);
        if (mRootView != null) {
            model.setScrollPos(mRootView.getScrollY());
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.MenuFavoriteDisabled).setVisible(true);
        menu.findItem(R.id.MenuPrint).setVisible(true);
        menu.findItem(R.id.MenuNew).setVisible(true);
        menu.findItem(R.id.MenuEdit).setVisible(true);
    }

    private void setScrollPos(int position) {
        if (mRootView == null) return;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mRootView.setScrollY(position));
    }

    public void setData(@NonNull FuseItem data)
    {
        if (mMainView == null || mTitleView == null) return;
        mMainView.removeAllViews();
        mTitleView.setText(data.getName());
        mCurrentLocation = mMainView;

        for (FuseItem child : data.getChilds()) {
            if (XML_LOCATION.equals(child.getTag())) {
                addLocation(child);
            }
        }
    }

    private void addLocation(FuseItem data)
    {
        if (getContext() == null) return;

        CollapsedLayout layout = new CollapsedLayout(getContext());
        layout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        layout.setHeaderColor(0xFFAAAAAA);
        layout.setTitle(data.getName());
        layout.setOnHeaderClickListener(this);
        layout.setExpanded(true);

        mMainView.addView(layout);
        mCurrentLocation = layout.getWrapperLayout();

        for (FuseItem child : data.getChilds())
        {
            if (XML_IMAGE.equals(child.getTag())) {
                addImage(child);
            } else if (XML_FUSE.equals(child.getTag())) {
                addFuse(child);
            }
        }

        mCurrentLocation = mMainView;
    }

    private void addImage(FuseItem data)
    {
        if (getContext() == null || mCurrentLocation == null) return;
        FuseImage image = new FuseImage(getContext());

        image.setAsset(data.getSrc());
        image.setOnImageClickListener((FuseImage.OnImageClickListener) getContext());
        image.setTag(data.getSrc());

        mCurrentLocation.addView(image);
    }

    private void addFuse(FuseItem data)
    {
        if (mCurrentLocation == null) return;

        ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.fuse_view, mMainView, false);
        ((TextView) viewGroup.findViewById(R.id.FuseID)).setText(data.getId());
        ((ImageView) viewGroup.findViewById(R.id.FuseIcon)).setImageResource(getFuseImageId(data.getCurrent()));
        ((TextView) viewGroup.findViewById(R.id.FuseName)).setText(data.getName());
        viewGroup.setBackgroundColor(getBackgroundColor(data.getCurrent()));

        mCurrentLocation.addView(viewGroup);
    }

    private int getColor(int id) {
        if (getContext() == null) return 0;
        return ContextCompat.getColor(getContext(), id);
    }

    private int getBackgroundColor(String current)
    {
        switch (current) {
            case "1A":
                return getColor(R.color.bg_1a);
            case "2A":
                return getColor(R.color.bg_2a);
            case "3A":
                return getColor(R.color.bg_3a);
            case "5A":
                return getColor(R.color.bg_5a);
            case "7.5A":
                return getColor(R.color.bg_7_5a);
            case "10A":
                return getColor(R.color.bg_10a);
            case "15A":
                return getColor(R.color.bg_15a);
            case "20A":
                return getColor(R.color.bg_20a);
            case "25A":
                return getColor(R.color.bg_25a);
            case "30A":
                return getColor(R.color.bg_30a);
            case "40A":
                return getColor(R.color.bg_40a);
            case "50A":
                return getColor(R.color.bg_50a);
            case "60A":
                return getColor(R.color.bg_60a);
            case "70A":
                return getColor(R.color.bg_70a);

            // Cylinder fuses
            case "C5A":
                return getColor(R.color.bg_c5a);
            case "C8A":
                return getColor(R.color.bg_c8a);
            case "C10A":
                return getColor(R.color.bg_c10a);
            case "C16A":
                return getColor(R.color.bg_c16a);
            case "C20A":
                return getColor(R.color.bg_c20a);
            case "C25A":
                return getColor(R.color.bg_c25a);
        }
        return Color.WHITE;
    }

    private int getFuseImageId(String current)
    {
        switch (current)
        {
            // Standard fuses
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

            // Cylinder fuses
            case "C5A":
                return R.drawable.fuse_c5a;
            case "C8A":
                return R.drawable.fuse_c8a;
            case "C10A":
                return R.drawable.fuse_c10a;
            case "C16A":
                return R.drawable.fuse_c16a;
            case "C20A":
                return R.drawable.fuse_c20a;
            case "C25A":
                return R.drawable.fuse_c25a;
        }
        return 0;
    }

    @Override
    public void onHeaderClick(CollapsedLayout layout) {
        if (layout.isExpanded()) {
            layout.collapse();
        } else {
            layout.expand();
        }
    }
}