package com.rokkystudio.fuse.menu;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuModel extends ViewModel
{
    private final MutableLiveData<NodeItem> mMenuData = new MutableLiveData<>();

    public void setMenu(NodeItem menu) {
        mMenuData.setValue(menu);
    }

    public MutableLiveData<NodeItem> getMenu() {
        return mMenuData;
    }
}
