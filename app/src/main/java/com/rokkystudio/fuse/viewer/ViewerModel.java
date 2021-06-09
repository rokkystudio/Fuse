package com.rokkystudio.fuse.viewer;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewerModel extends ViewModel
{
    private final MutableLiveData<Drawable> mDrawable = new MutableLiveData<>();

    public void setDrawable(@NonNull Drawable drawable) {
        mDrawable.setValue(drawable);
    }

    public MutableLiveData<Drawable> getDrawable() {
        return mDrawable;
    }

}