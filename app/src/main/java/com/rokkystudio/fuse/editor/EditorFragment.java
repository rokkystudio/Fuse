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
    private ViewGroup mMainView = null;
    private EditText mTitleEdit = null;

    private ViewGroup mContainer = null;
    private LayoutInflater mLayoutInflater = null;

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
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        mContainer = mMainView;

        for (FuseItem child : data.getChilds()) {
            if (XML_LOCATION.equals(child.getTag())) {
                addGroup(child);
            }
        }
    }

    private void addGroup(FuseItem data)
    {
        if (getContext() == null) return;
        EditorGroup group = new EditorGroup(getContext());
        group.setTitle(data.getName());
        mContainer.addView(group);
        mContainer = group.getContainer();

        for (FuseItem child : data.getChilds())
        {
            if (XML_IMAGE.equals(child.getTag())) {
                addImage(child);
            } else if (XML_FUSE.equals(child.getTag())) {
                addFuse(child);
            }
        }

        mContainer = mMainView;
    }
}
