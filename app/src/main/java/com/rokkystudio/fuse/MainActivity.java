package com.rokkystudio.fuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
            .add(R.id.MainFrame, FragmentFuses.newInstance(R.xml.bmw_e39_old))
            .commit();
    }
}