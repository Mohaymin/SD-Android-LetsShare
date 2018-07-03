package com.marsprojects.filetransfer;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    ImageView imageView;
    RelativeLayout newfoundDevice;
    AnimationDrawable animationDrawable;
    TextView searchorfoundTV;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 22) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

        newfoundDevice = (RelativeLayout) findViewById(R.id.newDeviceRL);
        newfoundDevice.setVisibility(View.INVISIBLE);
        searchorfoundTV = (TextView) findViewById(R.id.searchorfoundTV);

        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        imageView = (ImageView) findViewById(R.id.catanim);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();

        ;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                newfoundDevice.setVisibility(View.VISIBLE);
                searchorfoundTV.setText("Device Found!");
            }
        }, SPLASH_TIME_OUT);

        imageButton = (ImageButton) findViewById(R.id.newDeviceIB);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Chumki Calaca Aka pata\nSanji Hala dus ki tata" + "\n" +
                        "abbu aka ammu paca\nabbuke brownie khawai na :) ", Toast.LENGTH_LONG).show();
            }
        });


    }
}



