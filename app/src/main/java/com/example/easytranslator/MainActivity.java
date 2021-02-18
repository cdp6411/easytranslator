package com.example.easytranslator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private Button mButton1,mButton2;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAd1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkPermission();
        mButton1 = findViewById(R.id.button1);
        mButton2 = findViewById(R.id.button2);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,Result.class);
                startActivity(intent);
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                if (mInterstitialAd1.isLoaded()) {
                    mInterstitialAd1.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd1 = new InterstitialAd(this);
        mInterstitialAd1.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd1.loadAd(new AdRequest.Builder().build());

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
        View dialogView= inflater.inflate(R.layout.customdialog, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")

                .setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        ImageView img = (ImageView)findViewById(R.id.imageofspleshscreen);
//                        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
//                        img.startAnimation(aniFade);
//                        img.setVisibility(View.VISIBLE);
//                        img.startAnimation(aniFade);

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton("Rate us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "https://play.google.com/store/apps/details?id=com.instagram.android";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                Toast.makeText(MainActivity.this, "Thank you for rating", Toast.LENGTH_SHORT).show();
            }
        });
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdLoader.Builder builder1 = new AdLoader.Builder(
                this, "ca-app-pub-3940256099942544/2247696110");
        builder1.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                TemplateView template =dialogView.findViewById(R.id.my_template);
                template.setNativeAd(unifiedNativeAd);
            }
        });
        AdLoader adLoader = builder1.build();
        adLoader.loadAd(new AdRequest.Builder().build());

        builder.create();
        builder.show();
    }
    public void checkPermission(){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Please Allow All Permission to Use Application", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(MainActivity.this).setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE).check();
    }


}
