package com.rokkystudio.fuse.menu;

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

public class MenuFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";
    private MenuLayout mMenuLayout = null;
    private MenuFragment() {}

    public static MenuFragment newInstance(String xmlPath) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(FILENAME, xmlPath);
        fragment.setArguments(args);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mMenuLayout = new MenuLayout(getContext());
        mMenuLayout.setOnMenuClickListener((MenuLayout.OnMenuClickListener) getContext());

        MenuModel menuModel = new ViewModelProvider(this).get(MenuModel.class);
        menuModel.getMenu().observe(getViewLifecycleOwner(), mMenuLayout::attachMenu);

        final Point scroll = menuModel.getScroll().getValue();
        if (scroll != null) {
            mMenuLayout.post(() -> mMenuLayout.scrollTo(scroll.x, scroll.y));
        }
        return mMenuLayout;
    }

    @Override
    public void onDestroyView() {
        if (mMenuLayout != null) {
            MenuModel menuModel = new ViewModelProvider(this).get(MenuModel.class);
            menuModel.setScroll(new Point(mMenuLayout.getScrollX(), mMenuLayout.getScrollY()));
            mMenuLayout.setOnMenuClickListener(null);
            mMenuLayout.detachMenu();
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
        menu.findItem(R.id.MenuFavoriteDisabled).setVisible(false);
        menu.findItem(R.id.MenuPrint).setVisible(false);
        menu.findItem(R.id.MenuNew).setVisible(true);
        menu.findItem(R.id.MenuEdit).setVisible(false);
    }
}