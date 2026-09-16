package com.rokkystudio.fuse.editor;

import static com.rokkystudio.fuse.xml.FuseXml.XML_FUSE;
import static com.rokkystudio.fuse.xml.FuseXml.XML_IMAGE;
import static com.rokkystudio.fuse.xml.FuseXml.XML_LOCATION;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.xml.FuseItem;
import com.rokkystudio.fuse.xml.FuseXml;

public class EditorFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";

    private ViewGroup mRootView = null;
    private EditText mTitleEdit = null;
    private ViewGroup mMainContainer = null;
    private ViewGroup mCurrentContainer = null;

    @NonNull
    public static EditorFragment newInstance(String filename) {
        EditorFragment fragment = new EditorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILENAME, filename);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() == null) return;
        String path = getArguments().getString(FILENAME);

        EditorModel model = new ViewModelProvider(this).get(EditorModel.class);
        Context context = getContext();
        if (context != null) {
            FuseItem data = FuseXml.parse(context, path);
            if (data != null) model.setFuseData(data);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = (ViewGroup) inflater.inflate(R.layout.editor_fragment, container, false);
        mTitleEdit = mRootView.findViewById(R.id.EditorFragmentTitle);
        mMainContainer = mRootView.findViewById(R.id.EditorFragmentContainer);

        EditorModel model = new ViewModelProvider(this).get(EditorModel.class);
        model.getFuseData().observe(getViewLifecycleOwner(), this::setData);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setData(@NonNull FuseItem data)
    {
        if (mMainContainer == null || mTitleEdit == null) return;
        mMainContainer.removeAllViews();
        mTitleEdit.setText(data.getName());
        mCurrentContainer = mMainContainer;

        for (FuseItem child : data.getChilds()) {
            if (XML_LOCATION.equals(child.getTag())) {
                addGroup(child);
            }
        }
    }

    private void addGroup(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;

        EditorGroup group = new EditorGroup(getContext());
        group.setTitle(data.getName());
        mCurrentContainer.addView(group);
        mCurrentContainer = group.getContainer();

        for (FuseItem child : data.getChilds())
        {
            if (XML_IMAGE.equals(child.getTag())) {
                addImage(child);
            } else if (XML_FUSE.equals(child.getTag())) {
                addFuse(child);
            }
        }

        mCurrentContainer = mMainContainer;
    }

    private void addImage(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;
        EditorImage image = new EditorImage(getContext());

        image.setAsset(data.getSrc());
        image.setTag(data.getSrc());

        mCurrentContainer.addView(image);
    }

    private void addFuse(FuseItem data)
    {
        if (getContext() == null || mCurrentContainer == null) return;

        EditorFuse fuse = new EditorFuse(getContext());
        // fuse.setFuseIcon(getFuseImageId(data.getCurrent()));
        // fuse.setBackgroundColor(getBackgroundColor(data.getCurrent()));

        mCurrentContainer.addView(fuse);
    }
}
