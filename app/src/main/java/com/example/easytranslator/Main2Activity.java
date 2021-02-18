package com.example.easytranslator;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements RecognitionListener{
    private AdView mAdView;
    private ImageButton select_audio;
    private TextView mTxt;
    private SpeechRecognizer mSpeechRecognizer;
    private MediaPlayer mp;
    private ImageButton mPlay;
    private ImageButton mPause;
    private Uri uri;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        select_audio = findViewById(R.id.select_audio);
        mPlay = findViewById(R.id.play);
        mPause = findViewById(R.id.pause);

        mTxt = findViewById(R.id.txt);
        select_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select audio"), 1);


            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp = new MediaPlayer();
                try {
                    mp.setDataSource(Main2Activity.this,uri); // here file is the location of the audio file you wish to use an input
                    mp.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listen();

                mPlay.setVisibility(View.GONE);

            }
        });


        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mPlay.setVisibility(View.VISIBLE);

            }
        });
        mTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(Main2Activity.this, "Select the audio file", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                uri = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void listen() {



        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);

        Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mp.start();
            }

        }, 2000);

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        mp.pause();
        mSpeechRecognizer.destroy();
        mSpeechRecognizer = null;
        listen();
    }

    @Override
    public void onResults(Bundle results) {
        //getting all the matches
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        //displaying the first match
        if (matches != null)
            mTxt.append(matches.get(0) + " ");

        mp.pause();
        mSpeechRecognizer.destroy();
        mSpeechRecognizer = null;
        listen();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onBackPressed() {

        if(mp!=null)
        {
            mp.stop();
        }
        super.onBackPressed();
    }
}
