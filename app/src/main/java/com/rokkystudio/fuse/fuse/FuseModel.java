package com.rokkystudio.fuse.fuse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FuseModel extends ViewModel
{
    private final MutableLiveData<FuseItem> mFuseData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mScrollPos = new MutableLiveData<>();

    public void setFuseData(FuseItem data) {
        mFuseData.setValue(data);
    }

    public MutableLiveData<FuseItem> getFuseData() {
        return mFuseData;
    }

    public void setScrollPos(Integer position) {
        mScrollPos.setValue(position);
    }

    public MutableLiveData<Integer> getScrollPos() {
        return mScrollPos;
    }
}
