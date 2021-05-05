package com.rokkystudio.fuse;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rokkystudio.fuse.views.DiagramView;
import com.rokkystudio.fuse.views.FuseLayout;

public class FragmentFuses extends Fragment implements DiagramView.OnDiagramClickListener
{
    private static final String XML_FILENAME = "XML_FILENAME";
    private View mRootView = null;
    private FuseLayout mFuseLayout = null;
    private String mXmlFileName = "";
    private DiagramView.OnDiagramClickListener mOnDiagramClickListener = null;

    @NonNull
    public static FragmentFuses newInstance(String filename) {
        FragmentFuses fragment = new FragmentFuses();
        Bundle bundle = new Bundle();
        bundle.putString(XML_FILENAME, filename);
        fragment.setArguments(bundle);
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
        mFuseLayout.setOnDiagramClickListener(this);
        return mRootView;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try {
            mOnDiagramClickListener = (DiagramView.OnDiagramClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDiagramClickListener");
        }
    }

    @Override
    public void onDiagramClick(String filename) {
        mOnDiagramClickListener.onDiagramClick(filename);
    }
}