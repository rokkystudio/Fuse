package com.rokkystudio.fuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentFuses extends Fragment
{
    private static final String XML_RESOURCE = "XML_RESOURCE";
    private View mRootView = null;
    private FuseLayout mFuseLayout = null;
    private int mResourse = 0;

    public static FragmentFuses newInstance(int resource) {
        FragmentFuses fragment = new FragmentFuses();
        Bundle args = new Bundle();
        args.putInt(XML_RESOURCE, resource);
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
            mResourse = getArguments().getInt(XML_RESOURCE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) return mRootView;
        mRootView = inflater.inflate(R.layout.fragment_fuses, container, false);
        mFuseLayout = mRootView.findViewById(R.id.FuseTable);
        mFuseLayout.loadXml(mResourse);
        return mRootView;
    }
}