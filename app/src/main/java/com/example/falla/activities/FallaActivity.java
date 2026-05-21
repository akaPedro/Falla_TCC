package com.example.falla.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.falla.R;

import java.util.Locale;

public class FallaActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageView voltaria;
    private android.view.View btnFallar;
    private EditText campoTexto;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_falla);

        voltaria = findViewById(R.id.btn_voltar_ia);
        btnFallar = findViewById(R.id.btn_falar);
        campoTexto = findViewById(R.id.edt_fala_ia);

        // 2. Inicializa o motor de voz
        tts = new TextToSpeech(this, this);

        // Gatilho para falar o que está no EditText
        btnFallar.setOnClickListener(v -> {
            if (campoTexto != null && campoTexto.getText() != null) {
                String textoParaFalar = campoTexto.getText().toString();
                falarTexto(textoParaFalar);

            }
        });

        voltaria.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.falla), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("pt", "BR"));
        } else {
            Log.e("TTS", "Falha na inicialização");
        }
    }

    public void falarTexto(String texto) {
        if (tts != null && !texto.trim().isEmpty()) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "falla_id_" + System.currentTimeMillis());
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}