package com.rokkystudio.fuse.menu;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

import static com.rokkystudio.fuse.menu.MenuXml.XML_FOLDER;
import static com.rokkystudio.fuse.menu.MenuXml.XML_ITEM;
import static com.rokkystudio.fuse.menu.MenuXml.XML_MENU;

public class MenuLayout extends ScrollView implements NodeItem.OnNodeClickListener
{
    private TextView mNoDataText = null;
    private ViewGroup mMenuWrapper = null;
    private NodeItem mRootItem = null;

    private OnMenuClickListener mMenuClickListener = null;

    public MenuLayout(Context context) {
        super(context);
        init();
    }

    public MenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MenuLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        setFillViewport(true);
        setLayoutParams(new ScrollView.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        inflate(getContext(), R.layout.menu_layout, this);
        mMenuWrapper = findViewById(R.id.MenuWrapper);
        mNoDataText = findViewById(R.id.NoDataText);
    }

    public void attachMenu(NodeItem menu)
    {
        if (menu == null) return;
        detachMenu();

        mNoDataText.setVisibility(GONE);
        mMenuWrapper.setVisibility(VISIBLE);
        mMenuWrapper.addView(menu.getView(getContext()));
        menu.setOnNodeClickListener(this);
    }

    public void detachMenu() {
        if (mRootItem != null) {
            mRootItem.setOnNodeClickListener(null);
            mRootItem.removeViews();
        }
        mMenuWrapper.removeAllViews();
        mNoDataText.setVisibility(VISIBLE);
        mMenuWrapper.setVisibility(GONE);
        mRootItem = null;
    }

    // TODO ADD LINE
    private void addLine() {
        // mCurrentLayout.addView(mLayoutInflater.inflate(R.layout.line, this, false));
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    @Override
    public void onNodeClick(NodeItem nodeItem)
    {
        if (XML_MENU.equals(nodeItem.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick(nodeItem.getName(), nodeItem.getLink());
            }
            return;
        }

        if (XML_ITEM.equals(nodeItem.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onItemClick(nodeItem.getName(), nodeItem.getLink());
            }
            return;
        }

        if (XML_FOLDER.equals(nodeItem.getTag())) {
            if (nodeItem.isExpanded()) {
                nodeItem.getView(getContext()).collapse();
            } else {
                nodeItem.getView(getContext()).expand();
            }
        }
    }

    public interface OnMenuClickListener {
        void onMenuClick(String name, String link);
        void onItemClick(String name, String link);
    }
}
