package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rokkystudio.fuse.editor.EditorFragment;
import com.rokkystudio.fuse.fuse.FuseFragment;
import com.rokkystudio.fuse.fuse.FuseImage;
import com.rokkystudio.fuse.viewer.ViewerFragment;
import com.rokkystudio.fuse.menu.MenuFragment;
import com.rokkystudio.fuse.menu.MenuLayout;

public class MainActivity extends AppCompatActivity implements
        FuseImage.OnImageClickListener, MenuLayout.OnMenuClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // При восстановлении активити (повороте экрана) ничего не делать
        if (savedInstanceState != null) return;

        /*
        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, MenuFragment.newInstance("menu.xml"))
            .commit();
        */

        /*
        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, FuseFragment.newInstance("bmw/e23/1982.xml"))
            .commit();
        */

        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, EditorFragment.newInstance("bmw/e23/1982.xml"))
            .commit();
    }

    @Override
    public void onMenuClick(String name, String link) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, MenuFragment.newInstance(link))
            .addToBackStack(ViewerFragment.class.getName())
            .commit();
    }

    @Override
    public void onItemClick(String name, String link) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, FuseFragment.newInstance(link))
            .addToBackStack(ViewerFragment.class.getName())
            .commit();
    }

    @Override
    public void onImageClick(String asset) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, ViewerFragment.newInstance(asset))
            .addToBackStack(ViewerFragment.class.getName())
            .commit();
    }
}