package com.india.rapchik.Settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.gson.JsonElement;
import com.india.rapchik.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.india.rapchik.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import static android.app.Activity.RESULT_OK;
import static com.india.rapchik.SimpleClasses.Variables.clientAccessTokenAI;
import static com.india.rapchik.SimpleClasses.Variables.richieIntroStr;

/**
 * By Pratik
 */
public class MeetRichieFragmant extends RootFragment implements View.OnClickListener, AIListener {
    View view;
    Context context;

    //AI
    private AIService aiService;
    TextToSpeech tts;
    SpeechRecognizer speechRecognizer;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;
    private String uuid = UUID.randomUUID().toString();

    public MeetRichieFragmant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_meet_richie, container, false);
        context=getContext();
        view.findViewById(R.id.Goback).setOnClickListener(this);
        view.findViewById(R.id.rateAppTxt).setOnClickListener(this);
        view.findViewById(R.id.richieIcon).setOnClickListener(this);
        setUpTTS();
        //initV2Chatbot();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.rateAppTxt:
                openRateInPlaystore();
                break;
            case R.id.richieIcon:
                if(check_permissions()){
                    setupAI();
                    //openListener();
                    //sendMessageAI("Hello");
                }
                break;

        }
    }
    //AI
    public void setupAI(){
        Log.v("pratik","in setup AI");
        final AIConfiguration config = new AIConfiguration(clientAccessTokenAI,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(context, config);
        aiService.setListener(this);
        //////////AI Start////////
        aiService.startListening();


    }
    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        String resultQuery=result.getResolvedQuery();
        String action=result.getAction();

        Log.v("pratik","act=="+action);
        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        //Toast.makeText(this, "qry: "+resultQuery+" act: "+action, Toast.LENGTH_SHORT).show();
        Log.v("AI","qry: "+resultQuery+" act: "+action);


        tts.speak(result.getFulfillment().getSpeech(),TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void openRateInPlaystore(){
        final String appPackageName = context.getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void openListener(){
        /*Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(i, 101);
        } catch (Exception e) {
            Toast.makeText(context, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }*/

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(context);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "this package");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
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

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> res= results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Toast.makeText(context, ""+res.get(0), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

    }

    public void setUpTTS(){
        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

    }
    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.ai_agent);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();
            Log.v("pratik","projectId=="+projectId);

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
            Log.v("pratik","session=="+session);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("pratik", "setup error");
        }
    }

    private void sendMessageAI(String msg){
        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
        new RequestJavaV2Task(context, session, sessionsClient, queryInput).execute();
        Log.v("pratik", "ssend message ai");
    }

    public static void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.v("pratik", "V2 Bot Reply: " + botReply);

        } else {
            Log.v("pratik", "Bot Reply: Null");
        }
    }

    public void richieIntro(){
        tts.speak("I am Richie",TextToSpeech.QUEUE_FLUSH,null);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            tts.speak(thingsYouSaid.get(0),TextToSpeech.QUEUE_FLUSH,null);
        }
    }*/
}


class RequestJavaV2Task extends AsyncTask<Void, Void, DetectIntentResponse> {
    Context context;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;

    RequestJavaV2Task(Context context, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.context = context;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }

    @Override
    protected DetectIntentResponse doInBackground(Void... voids) {
        try{
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .build();
            Log.v("pratik", "doin background");
            Log.v("pratik", "queryInput="+queryInput);
            Log.v("pratik", "detectIntentRequest="+detectIntentRequest);
            return sessionsClient.detectIntent(detectIntentRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("pratik", "error doin: "+e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(DetectIntentResponse detectIntentResponse) {
        super.onPostExecute(detectIntentResponse);
        Log.v("pratik", "post exe");
        MeetRichieFragmant.callbackV2(detectIntentResponse);
    }
}