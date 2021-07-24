package com.rokkystudio.fuse.fuse;

import static com.rokkystudio.fuse.xml.FuseXml.XML_FUSE;
import static com.rokkystudio.fuse.xml.FuseXml.XML_IMAGE;
import static com.rokkystudio.fuse.xml.FuseXml.XML_LOCATION;

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
import android.widget.TextView;

import com.rokkystudio.fuse.CollapsedLayout;
import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.xml.FuseItem;
import com.rokkystudio.fuse.xml.FuseXml;

public class FuseFragment extends Fragment implements
    CollapsedLayout.OnHeaderClickListener
{
    private static final String FILENAME = "FILENAME";

    private ViewGroup mRootView = null;
    private TextView mTitleView = null;
    private ViewGroup mMainContainer = null;
    private ViewGroup mCurrentContainer = null;

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
            FuseItem data = FuseXml.parse(context, path);
            if (data != null) model.setFuseData(data);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fuse_fragment, container, false);
        mMainContainer = mRootView.findViewById(R.id.FuseFragmentContainer);
        mTitleView = mRootView.findViewById(R.id.FuseFragmentTitle);

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
        if (mMainContainer == null || mTitleView == null) return;
        mMainContainer.removeAllViews();
        mTitleView.setText(data.getName());
        mCurrentContainer = mMainContainer;

        for (FuseItem child : data.getChilds()) {
            if (XML_LOCATION.equals(child.getTag())) {
                addGroup(child);
            }
        }
    }

    private void addGroup(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;

        FuseGroup group = new FuseGroup(getContext());
        group.setTitle(data.getName());
        group.setOnHeaderClickListener(this);
        group.setExpanded(true);

        mCurrentContainer.addView(group);
        mCurrentContainer = group.getContainer();

        for (FuseItem child : data.getChilds())
        {
            if (XML_IMAGE.equals(child.getTag())) {
                addImage(child);
            } else if (XML_FUSE.equals(child.getTag())) {
                addFuse(child);
            }
        }

        mCurrentContainer = mMainContainer;
    }

    private void addImage(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;
        FuseImage image = new FuseImage(getContext());

        image.setAsset(data.getSrc());
        image.setOnImageClickListener((FuseImage.OnImageClickListener) getContext());
        image.setTag(data.getSrc());

        mCurrentContainer.addView(image);
    }

    private void addFuse(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;

        FuseView fuse = new FuseView(getContext());
        fuse.setFuseID(data.getId());
        fuse.setFuseIcon(getFuseImageId(data.getCurrent()));
        fuse.setFuseName(data.getName());
        fuse.setBackgroundColor(getBackgroundColor(data.getCurrent()));

        mCurrentContainer.addView(fuse);
    }

    private int getColor(int id) {
        if (getContext() == null) return 0;
        return ContextCompat.getColor(getContext(), id);
    }

    private int getBackgroundColor(String current)
    {
        switch (current) {
            case "1A":
                return getColor(R.color.BG_1A);
            case "2A":
                return getColor(R.color.BG_2A);
            case "3A":
                return getColor(R.color.BG_3A);
            case "5A":
                return getColor(R.color.BG_5A);
            case "7.5A":
                return getColor(R.color.BG_7_5A);
            case "10A":
                return getColor(R.color.BG_10A);
            case "15A":
                return getColor(R.color.BG_15A);
            case "20A":
                return getColor(R.color.BG_20A);
            case "25A":
                return getColor(R.color.BG_25A);
            case "30A":
                return getColor(R.color.BG_30A);
            case "40A":
                return getColor(R.color.BG_40A);
            case "50A":
                return getColor(R.color.BG_50A);
            case "60A":
                return getColor(R.color.BG_60A);
            case "70A":
                return getColor(R.color.BG_70A);

            // Cylinder fuses
            case "C5A":
                return getColor(R.color.BG_C5A);
            case "C8A":
                return getColor(R.color.BG_C8A);
            case "C10A":
                return getColor(R.color.BG_C10A);
            case "C16A":
                return getColor(R.color.BG_C16A);
            case "C20A":
                return getColor(R.color.BG_C20A);
            case "C25A":
                return getColor(R.color.BG_C25A);
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