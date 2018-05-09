package com.inception.translatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by DoguD on 01/07/2017.
 */

public class MainActivity extends AppCompatActivity {

    private LinearLayout pickersLayout ;

    final String[] from_arr = new String[] { "ENGLISH", "FRENCH","PUNJABI" , "GUJARATI" , "MARATHI","DANISH","POLISH","ITALIAN","SPANISH","TELUGU","TAMIL","RUSSIAN","URDU" , "GERMAN"} ;


    final String[] to_arr = new String[] { "ENGLISH", "FRENCH","PUNJABI" , "GUJARATI" , "MARATHI","DANISH","POLISH","ITALIAN","SPANISH","TELUGU","TAMIL","RUSSIAN","URDU" , "GERMAN"} ;

    final String[]  language_codes = new String[] { "en" , "fr" , "pa" , "gu" , "mr" , "da" , "pl" , "it" , "es" , "te" , "ta" , "ru" , "ur" , "de" } ;

    private  NumberPicker fromPicker , toPicker ;

    private TextView translated_txt ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickersLayout = findViewById(R.id.pickers_layout);

        translated_txt = findViewById(R.id.output_txt);


        fromPicker = new NumberPicker(this);
        fromPicker.setMaxValue(14);
        fromPicker.setMinValue(1);
        fromPicker.setScaleX(1.5f);
        fromPicker.setScaleY(1.5f);
//
        toPicker = new NumberPicker(this);
        toPicker.setMaxValue(14);
        toPicker.setMinValue(1);
        toPicker.setScaleX(1.5f);
        toPicker.setScaleY(1.5f);


        fromPicker.setDisplayedValues(from_arr);

        toPicker.setDisplayedValues(to_arr);
//

//
        LinearLayout.LayoutParams numPicerParams = new LinearLayout.LayoutParams(0, 200);
        numPicerParams.weight = 1;
//
        LinearLayout.LayoutParams qPicerParams = new LinearLayout.LayoutParams(0 , 200);
        qPicerParams.weight = 1;
//
        pickersLayout.addView(fromPicker,numPicerParams);


        pickersLayout.addView(toPicker,qPicerParams);





    }

    //Function for calling executing the Translator Background Task
    void Translate(String textToBeTranslated,String languagePair){
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(MainActivity.this);


        translatorBackgroundTask.execute(textToBeTranslated,languagePair); // Returns the translated text as a String
        //Log.d("Translation Result",translationResult); // Logs the result in Android Monitor
    }

    public void translate(View view) {


        EditText translate_text = findViewById(R.id.translate_this_et);

        //Default variables for translation
        String textToBeTranslated = translate_text.getText().toString();

        String from_lang = language_codes[fromPicker.getValue()-1];

        String to_lang = language_codes[toPicker.getValue()-1];

        String languagePair = from_lang+"-"+to_lang;
        //Executing the translation function
        Translate(textToBeTranslated,languagePair);

    }

    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
        //Declare Context
        Context ctx;

        //Set Context
        TranslatorBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;

            try {
                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20180509T213420Z.d002fa07bb4e9c8a.4d11ee882b7db30045e0e4015de133598dc21586";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + URLEncoder.encode(textToBeTranslated, "UTF-8") + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                String resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[') + 1);
                resultString = resultString.substring(0, resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"") + 1);
                resultString = resultString.substring(0, resultString.indexOf("\""));

                Log.d("Translation Result:", resultString);
                return resultString;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

            translated_txt.setText(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}


