package com.rokkystudio.fuse.menu;

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

import java.io.IOException;
import java.io.InputStream;

public class MenuFragment extends Fragment
{
    private static final String XML_PATH = "XML_PATH";
    private MenuModel mMenuModel;

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
        String xmlPath = getArguments().getString(XML_PATH);

        try {
            MenuXml menuXml = new MenuXml();
            String fullpath = getResources().getString(R.string.language) + "/" + xmlPath;
            InputStream inputStream = getContext().getAssets().open(fullpath);
            MenuModel model = new ViewModelProvider(this).get(MenuModel.class);
            model.setMenu(menuXml.parseStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MenuLayout layout = new MenuLayout(getContext());
        MenuModel model = new ViewModelProvider(this).get(MenuModel.class);
        model.getMenu().observe(getViewLifecycleOwner(), layout::setMenu);
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
}