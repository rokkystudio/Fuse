package com.rokkystudio.fuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDiagram extends Fragment
{
    private static final String DIAGRAM_FILENAME = "DIAGRAM_FILENAME";
    private String mDiagramFilename = null;
    private DiagramView mDiagramView = null;

    private FragmentDiagram() {}

    public static FragmentDiagram newInstance(String filename) {
        FragmentDiagram fragment = new FragmentDiagram();
        Bundle args = new Bundle();
        args.putString(DIAGRAM_FILENAME, filename);
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
            mDiagramFilename = getArguments().getString(DIAGRAM_FILENAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mDiagramView != null) return mDiagramView;
        mDiagramView = new DiagramView(getContext());
        mDiagramView.setImageFromAsset(mDiagramFilename);
        return mDiagramView;
    }
}