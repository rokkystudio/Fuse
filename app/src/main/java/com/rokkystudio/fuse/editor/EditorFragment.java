package com.rokkystudio.fuse.editor;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_FUSE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_IMAGE;
import static com.rokkystudio.fuse.fuse.FuseXml.XML_LOCATION;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rokkystudio.fuse.CollapsedLayout;
import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.fuse.FuseItem;
import com.rokkystudio.fuse.fuse.FuseXml;

public class EditorFragment extends Fragment
{
    private static final String FILENAME = "FILENAME";

    private ViewGroup mRootView = null;
    private ViewGroup mMainView = null;
    private EditText mTitleEdit = null;

    private ViewGroup mCurrentLocation = null;

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
        mMainView = mRootView.findViewById(R.id.EditorMain);
        mTitleEdit = mRootView.findViewById(R.id.EditorTitle);

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
        if (mMainView == null || mTitleEdit == null) return;
        mMainView.removeAllViews();
        mTitleEdit.setText(data.getName());
        mCurrentLocation = mMainView;

        for (FuseItem child : data.getChilds()) {
            if (XML_LOCATION.equals(child.getTag())) {
                addLocation(child);
            }
        }
    }

    private void addLocation(FuseItem data)
    {
        if (getContext() == null) return;

        CollapsedLayout layout = new CollapsedLayout(getContext());
        layout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        layout.setHeaderColor(0xFFAAAAAA);
        layout.setTitle(data.getName());
        layout.setOnHeaderClickListener(this);
        layout.setExpanded(true);

        mMainView.addView(layout);
        mCurrentLocation = layout.getWrapperLayout();

        for (FuseItem child : data.getChilds())
        {
            if (XML_IMAGE.equals(child.getTag())) {
                addImage(child);
            } else if (XML_FUSE.equals(child.getTag())) {
                addFuse(child);
            }
        }

        mCurrentLocation = mMainView;
    }
}
