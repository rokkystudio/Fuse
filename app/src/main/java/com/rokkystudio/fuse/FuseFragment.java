    package com.rokkystudio.fuse;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rokkystudio.fuse.diagram.DiagramView;
import com.rokkystudio.fuse.menu.MenuModel;
import com.rokkystudio.fuse.menu.MenuXml;
import com.rokkystudio.fuse.menu.NodeItem;
import com.rokkystudio.fuse.xml.FuseLayout;

    public class FuseFragment extends Fragment implements DiagramView.OnDiagramClickListener
{
    private static final String XML_PATH = "XML_PATH";
    private FuseLayout mFuseLayout = null;
    private DiagramView.OnDiagramClickListener mOnDiagramClickListener = null;

    @NonNull
    public static FuseFragment newInstance(String xmlPath) {
        FuseFragment fragment = new FuseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(XML_PATH, xmlPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() == null) return;
        String path = getArguments().getString(XML_PATH);

        MenuModel model = new ViewModelProvider(this).get(MenuModel.class);
        Context context = getContext();
        if (context != null) {
            NodeItem menu = MenuXml.parse(context, path);
            if (menu != null) model.setMenu(menu);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFuseLayout = new FuseLayout(getContext());
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

    @Override
    public void onDiagramClick(String filename) {
        mOnDiagramClickListener.onDiagramClick(filename);
    }
}