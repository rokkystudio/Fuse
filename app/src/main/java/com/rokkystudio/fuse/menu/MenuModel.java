package com.rokkystudio.fuse.menu;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuModel extends ViewModel
{
    private final MutableLiveData<NodeItem> mMenuData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mScroll = new MutableLiveData<>();

    public MenuModel() {
        mScroll.setValue(0);
    }

    public void setMenu(NodeItem menu) {
        mMenuData.setValue(menu);
    }

    public MutableLiveData<NodeItem> getMenu() {
        return mMenuData;
    }

    public void setScroll(int scroll) {
        mScroll.setValue(scroll);
    }

    public MutableLiveData<Integer> getScroll() {
        return mScroll;
    }
}
