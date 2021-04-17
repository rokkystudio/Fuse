package com.rokkystudio.fuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;

public class FragmentFuses extends Fragment
{
    private static final String XML_FILENAME = "XML_FILENAME";
    private View mRootView = null;
    private FuseLayout mFuseLayout = null;
    private String mXmlFileName = "";

    public static FragmentFuses newInstance(String filename) {
        FragmentFuses fragment = new FragmentFuses();
        Bundle args = new Bundle();
        args.putString(XML_FILENAME, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mXmlFileName = getArguments().getString(XML_FILENAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) return mRootView;
        mRootView = inflater.inflate(R.layout.fragment_fuses, container, false);
        mFuseLayout = mRootView.findViewById(R.id.FuseTable);
        mFuseLayout.loadXml(mXmlFileName);
        return mRootView;
    }
}