package com.example.easytranslator;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.easytranslator.api.APIHelper;
import com.example.easytranslator.api.Languages;
import com.example.easytranslator.api.TranslatedText;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;



public class Result extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private String mLanguageCodeFrom = "en";
    private String mLanguageCodeTo = "en";
    private static final int REQ_CODE_SPEECH_INPUT = 1;
    private boolean noTranslate;
    HashMap<String, String> map = new HashMap<>();
    private TextToSpeech mTextToSpeech;
    volatile boolean activityRunning;
    private InterstitialAd mInterstitialAd;

    public static final String LOG_TAG = Result.class.getName();
    private Spinner mSpinnerLanguageFrom;
    private ImageView mLaguageSwap;
    private Spinner mSpinnerLanguageTo;
    private EditText mTextInput;
    private ImageView mImageListen;
    private ImageView mSpeak;
    private ImageView mClearText;
    private Button mButtonTranslate;
    private TextView mTextTranslated;
    private ImageView mImageSpeak;
    private ImageView mCopyText;
    private ImageView mShareText;
    private TextView mEmptyViewNotConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        activityRunning = true;
        mSpinnerLanguageFrom = findViewById(R.id.spinner_language_from);
        mLaguageSwap = findViewById(R.id.laguage_swap);
        mSpinnerLanguageTo = findViewById(R.id.spinner_language_to);
        mTextInput = findViewById(R.id.text_input);
        mImageListen = findViewById(R.id.image_listen);
        mSpeak = findViewById(R.id.speak);
        mClearText = findViewById(R.id.clear_text);
        mButtonTranslate = findViewById(R.id.button_translate);
        mTextTranslated = findViewById(R.id.mTextTranslated);
        mImageSpeak = findViewById(R.id.image_speak);
        mCopyText = findViewById(R.id.copy_text);
        mShareText = findViewById(R.id.share_text);
        mEmptyViewNotConnected = findViewById(R.id.empty_view_not_connected);

        mTextInput.setMovementMethod(new ScrollingMovementMethod());
        mTextTranslated.setMovementMethod(new ScrollingMovementMethod());
        mTextToSpeech = new TextToSpeech(this,this);
        setSpinners();
        //textChangedListener();
        if (!isOnline()) {
            mEmptyViewNotConnected.setVisibility(View.VISIBLE);
        } else {
            mEmptyViewNotConnected.setVisibility(View.GONE);
        }


        mImageListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageCodeFrom);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextInput.setText("");
                mTextTranslated.setText("");
            }
        });

        mCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", mTextTranslated.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(Result.this, "Copied", Toast.LENGTH_SHORT).show();

            }
        });

        mShareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, mTextTranslated.getText().toString());
                shareintent.setType("text/plain");
                startActivity(shareintent);
            }
        });

        mImageSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakIn();

            }
        });
        mButtonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textChangedListener();
            }
        });


        mLaguageSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotate = new RotateAnimation(
                        0, 180,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );

                rotate.setDuration(300);
                mLaguageSwap.startAnimation(rotate);

                int sourceLng = mSpinnerLanguageFrom.getSelectedItemPosition();
                int targetLng = mSpinnerLanguageTo.getSelectedItemPosition();

                mSpinnerLanguageFrom.setSelection(targetLng);
                mSpinnerLanguageTo.setSelection(sourceLng);

                String textFrom = mTextInput.getText().toString();
                String textTo = mTextTranslated.getText().toString();

                mTextInput.setText(textTo);
                mTextTranslated.setText(textFrom);

                translate(mTextInput.getText().toString().trim());

            }
        });
    }



    //  CHECK INTERNET CONNECTION
    private boolean isOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTextInput.setText(matches_text.get(0));
                }
            }
        }
    }

    private void translate(String text) {
        if (noTranslate) {
            noTranslate = false;
            return;
        }

        String APIKey = "trnsl.1.1.20170314T200256Z.c558a20c3d6824ff.7" + "860377e797dffcf9ce4170e3c21266cbc696f08";
        String language1 = String.valueOf(mSpinnerLanguageFrom.getSelectedItem());
        String language2 = String.valueOf(mSpinnerLanguageTo.getSelectedItem());

        Retrofit query = new Retrofit.Builder().baseUrl("https://translate.yandex.net/").
                addConverterFactory(GsonConverterFactory.create()).build();
        APIHelper apiHelper = query.create(APIHelper.class);
        Call<TranslatedText> call = apiHelper.getTranslation(APIKey, text,
                langCode(language1) + "-" + langCode(language2));

        call.enqueue(new Callback<TranslatedText>() {
            @Override
            public void onResponse(Call<TranslatedText> call, final Response<TranslatedText> response) {
                if (response.isSuccessful()) {
                    mTextTranslated.setText(response.body().getText().get(0));

                }
            }

            @Override
            public void onFailure(Call<TranslatedText> call, Throwable t) {
            }
        });
    }

    public String langCode(String selectedLang) {
        String code = null;

        if (Locale.getDefault().getLanguage().equals("en")) {
            for (int i = 0; i < Languages.getLangsEN().length; i++) {
                if (selectedLang.equals(Languages.getLangsEN()[i])) {
                    code = Languages.getLangCodeEN(i);
                }
            }
        } else {
            for (int i = 0; i < Languages.getLangsRU().length; i++) {
                if (selectedLang.equals(Languages.getLangsRU()[i])) {
                    code = Languages.getLangCodeRU(i);
                }
            }
        }
        return code;
    }


    public void setSpinners() {
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        if (Locale.getDefault().getLanguage().equals("en")) {
            Collections.addAll(categories, Languages.getLangsEN());
        } else {
            Collections.addAll(categories, Languages.getLangsRU());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Result.this,
                android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerLanguageFrom.setAdapter(dataAdapter);
        mSpinnerLanguageTo.setAdapter(dataAdapter);
        mSpinnerLanguageTo.setSelection(0);
    }

    private void speakOut() {
        int result = mTextToSpeech.setLanguage(new Locale(mLanguageCodeTo));
        Log.e("Inside", "speakOut " + mLanguageCodeTo + " " + result);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(getApplicationContext(), getString(R.string.language_pack_missing), Toast.LENGTH_SHORT).show();
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
        } else {
            String textMessage = mTextTranslated.getText().toString();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            mTextToSpeech.speak(textMessage, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    private void speakIn() {
        int result = mTextToSpeech.setLanguage(new Locale(mLanguageCodeTo));
        Log.e("Inside", "speakOut " + mLanguageCodeTo + " " + result);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(getApplicationContext(), getString(R.string.language_pack_missing), Toast.LENGTH_SHORT).show();
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
        } else {
            String textMessage = mTextInput.getText().toString();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            mTextToSpeech.speak(textMessage, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    @Override
    public void onInit(int status) {
        Log.e("Inside----->", "spekaOut");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(new Locale("en"));
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Toast.makeText(getApplicationContext(), getString(R.string.language_pack_missing), Toast.LENGTH_SHORT).show();
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.language_not_supported), Toast.LENGTH_SHORT).show();
            }
            mImageSpeak.setEnabled(true);
            mSpeak.setEnabled(true);
            mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.e("Inside", "OnStart");

                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        } else {
            Log.e(LOG_TAG, "TTS Initilization Failed");
        }
    }

    @Override
    protected void onPause() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
        }
        super.onPause();
    }

    //  WHEN ACTIVITY IS DESTROYED
    @Override
    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        activityRunning = false;
        super.onDestroy();
    }

    public void textChangedListener() {

        // Translate the text after 500 milliseconds when user ends to typing
        RxTextView.textChanges(mTextInput).
                filter(charSequence -> charSequence.length() > 0).
                debounce(300, TimeUnit.MILLISECONDS).
                subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        translate(charSequence.toString().trim());
                    }
                });
    }

}
