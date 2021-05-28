package com.rokkystudio.fuse.menu;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuModel extends ViewModel
{
    private final MutableLiveData<MenuNode> mMenuData = new MutableLiveData<>();

    public void setMenu(MenuNode menu) {
        mMenuData.setValue(menu);
    }

    public MutableLiveData<MenuNode> getMenu() {
        return mMenuData;
    }
}
