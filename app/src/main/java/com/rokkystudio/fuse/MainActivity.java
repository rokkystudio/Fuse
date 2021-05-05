package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rokkystudio.fuse.views.DiagramView;

public class MainActivity extends AppCompatActivity implements DiagramView.OnDiagramClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // При восстановлении активити (повороте экрана) ничего не делать
        if (savedInstanceState != null) return;

        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, FragmentFuses.newInstance("bmw/e39_1996_1998.xml"))
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