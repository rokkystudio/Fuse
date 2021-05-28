package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rokkystudio.fuse.menu.MenuFragment;

public class MainActivity extends AppCompatActivity implements DiagramView.OnDiagramClickListener
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
            .replace(R.id.MainFrame, FragmentDiagram.newInstance(filename))
            .addToBackStack(FragmentDiagram.class.getName())
            .commit();
    }
}