package com.rokkystudio.fuse.viewer;

import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

public class ViewerFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";

    private ViewerFragment() {}

    public static ViewerFragment newInstance(String filename) {
        ViewerFragment fragment = new ViewerFragment();
        Bundle args = new Bundle();
        args.putString(FILENAME, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() == null) return;
        String filename = getArguments().getString(FILENAME);

        if (getContext() == null) return;
        AssetManager assetManager = getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open(filename);
            Drawable drawable = BitmapDrawable.createFromStream(inputStream, null);
            ViewerModel model = new ViewModelProvider(this).get(ViewerModel.class);
            model.setDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewerView viewer = new ViewerView(getContext());
        ViewerModel model = new ViewModelProvider(this).get(ViewerModel.class);
        model.getDrawable().observe(getViewLifecycleOwner(), viewer::setDrawable);
        return viewer;
    }
}