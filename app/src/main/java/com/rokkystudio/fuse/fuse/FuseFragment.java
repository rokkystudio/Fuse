package com.rokkystudio.fuse.fuse;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rokkystudio.fuse.R;

public class FuseFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";
    private FuseLayout mFuseLayout = null;

    @NonNull
    public static FuseFragment newInstance(String xmlPath) {
        FuseFragment fragment = new FuseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILENAME, xmlPath);
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
        mFuseLayout = new FuseLayout(getContext());
        FuseModel model = new ViewModelProvider(this).get(FuseModel.class);
        model.getFuseData().observe(getViewLifecycleOwner(), mFuseLayout::setData);
        model.getScrollPos().observe(getViewLifecycleOwner(), this::setScrollPos);
        mFuseLayout.setOnImageClickListener((FuseImage.OnImageClickListener) getContext());
        return mFuseLayout;
    }

    @Override
    public void onDestroyView() {
        FuseModel model = new ViewModelProvider(this).get(FuseModel.class);
        if (mFuseLayout != null) {
            model.setScrollPos(mFuseLayout.getScrollY());
            mFuseLayout.setOnImageClickListener(null);
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

    private void setScrollPos(int position)
    {
        if (mFuseLayout == null) return;
        new Handler().post(() -> {
            mFuseLayout.setScrollY(position);
        });
    }
}