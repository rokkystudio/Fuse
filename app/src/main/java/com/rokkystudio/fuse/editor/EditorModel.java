package com.rokkystudio.fuse.editor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rokkystudio.fuse.fuse.FuseItem;

public class EditorModel extends ViewModel
{
    private final MutableLiveData<FuseItem> mFuseData = new MutableLiveData<>();

    public void setFuseData(FuseItem data) {
        mFuseData.setValue(data);
    }

    public MutableLiveData<FuseItem> getFuseData() {
        return mFuseData;
    }
}
