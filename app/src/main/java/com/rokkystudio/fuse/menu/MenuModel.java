package com.rokkystudio.fuse.menu;

import android.graphics.Point;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuModel extends ViewModel
{
    private final MutableLiveData<MenuItem> mMenuData = new MutableLiveData<>();
    private final MutableLiveData<Point> mScroll = new MutableLiveData<>();

    public void setMenu(MenuItem menu) {
        mMenuData.setValue(menu);
    }

    public MutableLiveData<MenuItem> getMenu() {
        return mMenuData;
    }

    public void setScroll(Point scroll) {
        mScroll.setValue(scroll);
    }

    public MutableLiveData<Point> getScroll() {
        return mScroll;
    }
}
