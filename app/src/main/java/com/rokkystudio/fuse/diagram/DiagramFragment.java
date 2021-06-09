package com.rokkystudio.fuse.diagram;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.menu.MenuModel;
import com.rokkystudio.fuse.menu.MenuXml;
import com.rokkystudio.fuse.menu.NodeItem;

public class DiagramFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";
    private DiagramLayout mDiagramLayout = null;

    @NonNull
    public static DiagramFragment newInstance(String xmlPath) {
        DiagramFragment fragment = new DiagramFragment();
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

        MenuModel model = new ViewModelProvider(this).get(MenuModel.class);
        Context context = getContext();
        if (context != null) {
            NodeItem menu = MenuXml.parse(context, path);
            if (menu != null) model.setMenu(menu);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDiagramLayout = new DiagramLayout(getContext());
        mDiagramLayout.loadXml(mXmlFileName);
        mDiagramLayout.setOnImageClickListener((DiagramLayout.OnImageClickListener) getContext());
        return mDiagramLayout;
    }

    @Override
    public void onDestroyView() {
        if (mDiagramLayout != null) {
            mDiagramLayout.setOnImageClickListener(null);
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
}