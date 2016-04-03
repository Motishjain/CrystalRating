package com.example.admin.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;

import com.example.admin.constants.AppConstants;

import java.util.Locale;

/**
 * Created by Admin on 3/31/2016.
 */
public class TextToSpeechConversionTask extends AsyncTask<String,Void,Void> {

    String text;
    Context context;
    TextToSpeech textToSpeechConverter;

    public TextToSpeechConversionTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(final String... text) {
        textToSpeechConverter = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    {
                        textToSpeechConverter.setPitch(0.7f); // saw from internet
                        textToSpeechConverter.setSpeechRate(1.0f); // f denotes float, it actually type casts 0.5 to float
                        textToSpeechConverter.setLanguage(Locale.UK);
                        textToSpeechConverter.speak(text[0], TextToSpeech.QUEUE_FLUSH, null);
                    }
            }
        }});
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.i("TextToSpeechConverter", "onPreExecute Called");
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i("TextToSpeechConverter", "onPostExecute Called");
        textToSpeechConverter.stop();
    }


}
