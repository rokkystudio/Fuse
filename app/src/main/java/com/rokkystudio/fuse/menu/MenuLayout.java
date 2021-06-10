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

public class MenuLayout extends ScrollView implements MenuItem.OnItemClickListener
{
    private TextView mNoDataText = null;
    private ViewGroup mMenuWrapper = null;
    private MenuItem mRootItem = null;

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

    public void attachMenu(MenuItem menu)
    {
        if (menu == null) return;
        detachMenu();

        mNoDataText.setVisibility(GONE);
        mMenuWrapper.setVisibility(VISIBLE);
        mMenuWrapper.addView(menu.getView(getContext()));
        menu.setOnItemClickListener(this);
    }

    public void detachMenu() {
        if (mRootItem != null) {
            mRootItem.setOnItemClickListener(null);
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
    public void onItemClick(MenuItem item)
    {
        if (XML_MENU.equals(item.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick(item.getName(), item.getLink());
            }
            return;
        }

        if (XML_ITEM.equals(item.getTag())) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onItemClick(item.getName(), item.getLink());
            }
            return;
        }

        if (XML_FOLDER.equals(item.getTag())) {
            if (item.isExpanded()) {
                item.getView(getContext()).collapse();
            } else {
                item.getView(getContext()).expand();
            }
        }
    }

    public interface OnMenuClickListener {
        void onMenuClick(String name, String link);
        void onItemClick(String name, String link);
    }
}
