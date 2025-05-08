package com.example.ainotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextView tvTranscription, tvSummary;
    private Button btnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        tvTranscription = findViewById(R.id.tvTranscription);
        tvSummary = findViewById(R.id.tvSummary);
        btnRecord = findViewById(R.id.btnRecord);

        // Vérifier et demander les permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        } else {
            initializeSpeechRecognizer();
        }

        // Définir un écouteur pour le bouton
        btnRecord.setOnClickListener(v -> {
            if (btnRecord.getText().toString().equals("Démarrer l'enregistrement")) {
                btnRecord.setText("Arrêter l'enregistrement");
                startListening();
            } else {
                btnRecord.setText("Démarrer l'enregistrement");
                stopListening();
            }
        });
    }

    private void initializeSpeechRecognizer() {
        // Initialiser SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Configurer l'intention pour la reconnaissance vocale
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); // Activer les résultats partiels

        // Gérer les résultats de reconnaissance vocale
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("MainActivity", "Prêt à enregistrer...");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("MainActivity", "Début de l'enregistrement...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.d("MainActivity", "Fin de l'enregistrement...");
            }

            @Override
            public void onError(int error) {
                Log.e("MainActivity", "Erreur lors de l'enregistrement : " + error);
                Toast.makeText(MainActivity.this, "Erreur : " + error, Toast.LENGTH_SHORT).show();

                // Redémarrer l'écoute après une erreur
                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    startListening();
                }
            }

            @Override
            public void onResults(Bundle results) {
                Log.d("MainActivity", "Résultats reçus...");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String transcription = matches.get(0);
                    Log.d("MainActivity", "Transcription reçue : " + transcription);
                    tvTranscription.setText("Transcription : " + transcription);
                } else {
                    Log.w("MainActivity", "Aucun résultat trouvé.");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startListening() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée : Microphone", Toast.LENGTH_SHORT).show();
                initializeSpeechRecognizer();
            } else {
                Toast.makeText(this, "Permission refusée : Microphone", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy(); // Libérer les ressources
        }
    }
}