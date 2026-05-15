package com.example.falla.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.falla.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private ImageView ImgFll;
    private ImageView ImgPerf;
    private DrawerLayout drawerLayout;
    private AppCompatImageView imgMenu;
    // Itens da barra lateral
    private TextView itemTamanho, itemCores, itemHistorico, itemSobre;
    private LinearLayout headerPessoal;
    private ImageView setaPessoal;
    private GridLayout conteudoPessoal;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ImgFll = findViewById(R.id.img_keyboard);
        ImgPerf = findViewById(R.id.img_profile);
        drawerLayout = findViewById(R.id.main);
        imgMenu = findViewById(R.id.img_menu);
        itemTamanho = findViewById(R.id.item_tamanho);
        itemCores = findViewById(R.id.item_cores);
        itemHistorico = findViewById(R.id.item_historico);
        itemSobre = findViewById(R.id.item_sobre);
        headerPessoal = findViewById(R.id.header_pessoal);
        conteudoPessoal = findViewById(R.id.conteudo_pessoal);
        setaPessoal = findViewById(R.id.seta_pessoal);

        // #####  cards principais ##### //

        // Card Sim
        LinearLayout headerCardSim = findViewById(R.id.header_card_sim);
        LinearLayout conteudoCardSim = findViewById(R.id.conteudo_card_sim);
        TextView txtSetaSim = findViewById(R.id.txt_seta_sim);
        ImageView iconSim = findViewById(R.id.img_sim_icon);

        // Card Não
        LinearLayout headerCardNao = findViewById(R.id.header_card_nao);
        LinearLayout conteudoCardNao = findViewById(R.id.conteudo_card_nao);
        TextView txtSetaNao = findViewById(R.id.txt_seta_nao);
        ImageView iconNao = findViewById(R.id.img_nao_icon);

        // Configurar as interações principais
        configurarGavetaInternaCard(headerCardSim, conteudoCardSim, txtSetaSim, ">", "v");
        configurarGavetaInternaCard(headerCardNao, conteudoCardNao, txtSetaNao, ">", "v");

        // Se clicar direto na imagem do V/X, fala a palavra principal
        iconSim.setOnClickListener(v -> falar("Sim"));
        iconNao.setOnClickListener(v -> falar("Não"));

        // Falas do Card Sim
        findViewById(R.id.item_sim_quero).setOnClickListener(v -> falar("Sim, eu quero"));
        findViewById(R.id.item_sim_gosto).setOnClickListener(v -> falar("Sim, eu gosto"));
        findViewById(R.id.item_sim_bom).setOnClickListener(v -> falar("Isso é bom"));

        // Falas do Card Não
        findViewById(R.id.item_nao_quero).setOnClickListener(v -> falar("Não, eu não quero"));
        findViewById(R.id.item_nao_gosto).setOnClickListener(v -> falar("Não, eu não gosto"));
        findViewById(R.id.item_nao_ruim).setOnClickListener(v -> falar("Isso é ruim"));



        // #### Barra latreral #### //
        itemTamanho.setOnClickListener(v -> {
            // Lógica para abrir configuração de tamanho
            Toast.makeText(this, "Ajustar botões", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers(); // Fecha a barra lateral após o clique
        });

        itemCores.setOnClickListener(v -> {
            // Lógica para cores
            drawerLayout.closeDrawers();
        });

        itemHistorico.setOnClickListener(v -> {
            // Lógica para histórico
            drawerLayout.closeDrawers();
        });

        itemSobre.setOnClickListener(v -> {
            // Abrir uma Activity ou Dialog de "Sobre"
            drawerLayout.closeDrawers();
        });


        // Botões de navegação
        imgMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        ImgFll.setOnClickListener(v -> {
            Intent fallintent = new Intent(MainActivity.this, FallaActivity.class);
            startActivity(fallintent);
        });

        ImgPerf.setOnClickListener(v -> {
            Intent perfintent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(perfintent);
        });

        itemTamanho.setOnClickListener(v -> {
            Toast.makeText(this, "Ajustar Tamanho", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        itemCores.setOnClickListener(v -> {
            // Sua lógica de cores aqui
            drawerLayout.closeDrawer(GravityCompat.START);
        });


        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Define o idioma para Português Brasil
                int result = tts.setLanguage(new Locale("pt", "BR"));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Idioma não suportado ou faltando dados.");
                }
            } else {
                Log.e("TTS", "Falha na inicialização!");
            }
        });

        // #####  HEADERS ##### //

//        configurarGaveta(findViewById(R.id.header_favoritos), findViewById(R.id.grid_favoritos), findViewById(R.id.txt_seta_favoritos));
//        configurarGaveta(findViewById(R.id.header_pessoal), findViewById(R.id.grid_pessoal), findViewById(R.id.txt_seta_pessoal));
//        configurarGaveta(findViewById(R.id.header_comidas), findViewById(R.id.grid_comidas), findViewById(R.id.txt_seta_comidas));


        headerPessoal.setOnClickListener(v -> {
            if (conteudoPessoal.getVisibility() == View.GONE) {
                conteudoPessoal.setVisibility(View.VISIBLE);
                setaPessoal.setRotation(90f); // Gira a seta para baixo
            } else {
                conteudoPessoal.setVisibility(View.GONE);
                setaPessoal.setRotation(0f); // Seta volta para a direita
            }
        });




        // Botao voltar padrão
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            View content = findViewById(R.id.layout_conteudo_cinza);
            content.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });
    }

    private void configurarGavetaInternaCard(LinearLayout header, LinearLayout conteudo, TextView seta, String textoPadrao, String textoExpandido) {
        header.setOnClickListener(v -> {
            if (conteudo.getVisibility() == View.GONE) {
                conteudo.setVisibility(View.VISIBLE);
                seta.setText(textoExpandido); // Ex: "v sim"
            } else {
                conteudo.setVisibility(View.GONE);
                seta.setText(textoPadrao); // Ex: "> sim"
            }
        });
    }

    // #####  FALAR ##### //
    private void falar(String texto) {
        if (tts != null) {
            // QUEUE_FLUSH limpa a fila e fala agora
            // QUEUE_ADD terminaria de falar o atual para depois falar o novo
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
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

//    private void setupGaveta(int headerId, int gridId, String titulo) {
//        LinearLayout header = findViewById(headerId);
//        GridLayout grid = findViewById(gridId);
//        TextView txtTitulo = header.findViewById(R.id.txt_titulo_gaveta);
//        ImageView seta = header.findViewById(R.id.img_seta);
//
//        txtTitulo.setText(titulo);
//
//        header.setOnClickListener(v -> {
//            if (grid.getVisibility() == View.GONE) {
//                grid.setVisibility(View.VISIBLE);
//                seta.setRotation(90f); // Gira a seta para baixo
//            } else {
//                grid.setVisibility(View.GONE);
//                seta.setRotation(0f);
//            }
//        });
//    }

}