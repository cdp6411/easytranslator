package com.example.easytranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;

public class spleshscreen extends AppCompatActivity {
    private static  int SPLESH_TIME_OUT=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spleshscreen);
//        MobileAds.initialize(this, getString(R.string.admob_app_id));

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeintent= new Intent(spleshscreen.this,MainActivity.class);
                startActivity(homeintent);
                finish();
            }

        },SPLESH_TIME_OUT);
        ImageView img = (ImageView)findViewById(R.id.imageofspleshscreen);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        img.startAnimation(aniFade);
    }
}

