package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rokkystudio.fuse.diagram.DiagramFragment;
import com.rokkystudio.fuse.diagram.DiagramView;
import com.rokkystudio.fuse.menu.MenuFragment;
import com.rokkystudio.fuse.menu.MenuLayout;

public class MainActivity extends AppCompatActivity implements
    DiagramView.OnDiagramClickListener, MenuLayout.OnMenuClickListener
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
    public void onDiagramClick(String filename) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, DiagramFragment.newInstance(filename))
            .addToBackStack(DiagramFragment.class.getName())
            .commit();
    }

    @Override
    public void onMenuClick(String name, String link) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, MenuFragment.newInstance(link))
            .addToBackStack(DiagramFragment.class.getName())
            .commit();
    }

    @Override
    public void onItemClick(String name, String link) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit)
            .replace(R.id.MainFrame, FuseFragment.newInstance(link))
            .addToBackStack(DiagramFragment.class.getName())
            .commit();
    }
}