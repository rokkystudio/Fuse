package com.rokkystudio.fuse.fuse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FuseModel extends ViewModel
{
    private final MutableLiveData<FuseItem> mFuseData = new MutableLiveData<>();

    public void setFuseData(FuseItem data) {
        mFuseData.setValue(data);
    }

    public MutableLiveData<FuseItem> getFuseData() {
        return mFuseData;
    }
}
