package com.rokkystudio.fuse.menu;

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

import com.rokkystudio.fuse.R;

public class MenuFragment extends Fragment implements MenuLayout.OnMenuClickListener
{
    private static final String XML_PATH = "XML_PATH";
    private MenuLayout.OnMenuClickListener mOnMenuClickListener = null;
    private MenuFragment() {}

    public static MenuFragment newInstance(String xmlPath) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(XML_PATH, xmlPath);
        fragment.setArguments(args);
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
            model.setMenu(MenuXml.parse(context, path));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MenuLayout layout = new MenuLayout(getContext());
        layout.setOnMenuClickListener(this);
        MenuModel model = new ViewModelProvider(this).get(MenuModel.class);
        model.getMenu().observe(getViewLifecycleOwner(), layout::setRootNode);
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.MenuFavoriteDisabled).setVisible(false);
        menu.findItem(R.id.MenuPrint).setVisible(false);
        menu.findItem(R.id.MenuNew).setVisible(true);
        menu.findItem(R.id.MenuEdit).setVisible(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mOnMenuClickListener = (MenuLayout.OnMenuClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnMenuClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMenuClickListener = null;
    }

    @Override
    public void onMenuClick(String name, String link) {
        if (mOnMenuClickListener != null) {
            mOnMenuClickListener.onMenuClick(name, link);
        }
    }

    @Override
    public void onItemClick(String name, String link) {
        if (mOnMenuClickListener != null) {
            mOnMenuClickListener.onItemClick(name, link);
        }
    }
}