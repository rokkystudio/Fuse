package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rokkystudio.fuse.diagram.DiagramFragment;
import com.rokkystudio.fuse.diagram.DiagramLayout;
import com.rokkystudio.fuse.viewer.ViewerFragment;
import com.rokkystudio.fuse.menu.MenuFragment;
import com.rokkystudio.fuse.menu.MenuLayout;

public class MainActivity extends AppCompatActivity implements
        DiagramLayout.OnImageClickListener, MenuLayout.OnMenuClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // При восстановлении активити (повороте экрана) ничего не делать
        if (savedInstanceState != null) return;

        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, MenuFragment.newInstance("menu.xml"))
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
            .replace(R.id.MainFrame, DiagramFragment.newInstance(link))
            .addToBackStack(ViewerFragment.class.getName())
            .commit();
    }

    @Override
    public void OnImageClick(String filename) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, ViewerFragment.newInstance(filename))
            .addToBackStack(ViewerFragment.class.getName())
            .commit();
    }
}