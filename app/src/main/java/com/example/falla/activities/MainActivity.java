package com.example.falla.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private ImageView ImgFll;
    private ImageView ImgPerf;
    private DrawerLayout drawerLayout;
    private AppCompatImageView imgMenu;
    // Itens da barra lateral
    private TextView itemTamanho, itemCores, itemHistorico, itemSobre;


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

        // Barra latreral
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
            drawerLayout.closeDrawer(GravityCompat.START); // Fecha após clicar
        });

        itemCores.setOnClickListener(v -> {
            // Sua lógica de cores aqui
            drawerLayout.closeDrawer(GravityCompat.START);
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
}