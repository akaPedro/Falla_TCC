package com.example.falla.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.falla.DAO.AppDatabase;
import com.example.falla.R;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout indicadores;
    private TextView btnAnterior, btnProximo;
    private android.widget.EditText edtNomeSlide = null;

    // Dados de cada slide: ícone, título, descrição
    private final int[] icones = {
            android.R.drawable.ic_menu_help,
            android.R.drawable.ic_menu_myplaces,
            android.R.drawable.ic_menu_sort_by_size,
            android.R.drawable.ic_menu_directions,
            android.R.drawable.btn_star_big_on,
            android.R.drawable.ic_menu_edit
    };

    private final String[] titulos = {
            "Bem-vindo ao Falla!",
            "Quem vai usar?",
            "Gavetas de comunicação",
            "Toque para falar",
            "Favoritos",
            "Tudo pronto!"
    };

    private final String[] descricoes = {
            "O Falla é um app de comunicação aumentativa e alternativa (CAA) feito para ajudar pessoas não verbais a se expressar com facilidade.",
            "Antes de começar, vá ao perfil (ícone redondo no canto superior direito) e cadastre o nome de quem vai usar o app.",
            "O app é organizado em gavetas por tema: Pessoal, Alimentos, Lazer e Aprendizado. Dentro de cada gaveta há subgavetas para facilitar a busca.",
            "Toque em qualquer card colorido e o app vai falar a palavra ou frase em voz alta usando o texto-para-fala do Android.",
            "Toque e segure um card para editá-lo, excluí-lo ou marcá-lo como favorito. Os favoritos ficam sempre no topo da tela.",
            "Você já pode começar a usar! Os cards já vêm pré-preenchidos. Você também pode adicionar os seus próprios usando o botão de teclado no topo."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        SharedPreferences prefs = getSharedPreferences("ConfigFalla", MODE_PRIVATE);
        if (prefs.getBoolean("onboarding_concluido", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        viewPager   = findViewById(R.id.viewpager_onboarding);
        indicadores = findViewById(R.id.indicadores_onboarding);
        btnAnterior = findViewById(R.id.btn_anterior_onboarding);
        btnProximo  = findViewById(R.id.btn_proximo_onboarding);

        viewPager.setAdapter(new OnboardingAdapter());
        construirIndicadores(0);



        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                construirIndicadores(position);
                atualizarBotoes(position);
            }
        });

        btnAnterior.setOnClickListener(v -> {
            int atual = viewPager.getCurrentItem();
            if (atual > 0) viewPager.setCurrentItem(atual - 1);
        });

        btnProximo.setOnClickListener(v -> {
            int atual = viewPager.getCurrentItem();
            if (atual < titulos.length - 1) {
                viewPager.setCurrentItem(atual + 1);
            } else {
                concluirOnboarding();
            }
        });

        atualizarBotoes(0);
        // Botão pular
        findViewById(R.id.btn_pular_onboarding).setOnClickListener(v -> concluirOnboarding());
    }

    private void construirIndicadores(int posicaoAtiva) {
        indicadores.removeAllViews();
        for (int i = 0; i < titulos.length; i++) {
            View ponto = new View(this);
            int tamanho = (int) (10 * getResources().getDisplayMetrics().density);
            int margem  = (int) (5  * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamanho, tamanho);
            params.setMargins(margem, 0, margem, 0);
            ponto.setLayoutParams(params);

            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            if (i == posicaoAtiva) {
                shape.setColor(0xFF2D4A43); // verde_fosco_header
                shape.setSize((int)(14 * getResources().getDisplayMetrics().density), tamanho);
                shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                shape.setCornerRadius(8 * getResources().getDisplayMetrics().density);
                params.width = (int)(28 * getResources().getDisplayMetrics().density);
                ponto.setLayoutParams(params);
            } else {
                shape.setColor(0xFFBFC5C2); // cinza suave
            }
            ponto.setBackground(shape);
            indicadores.addView(ponto);
        }
    }

    private void atualizarBotoes(int posicao) {
        btnAnterior.setVisibility(posicao == 0 ? View.INVISIBLE : View.VISIBLE);
        btnProximo.setText(posicao == titulos.length - 1 ? "Começar!" : "Próximo");
    }

    private void concluirOnboarding() {
        SharedPreferences prefs = getSharedPreferences("ConfigFalla", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_concluido", true).apply();

        // ✅ Salva o nome no banco se o usuário digitou
        String nome = (edtNomeSlide != null) ? edtNomeSlide.getText().toString().trim() : "";
        if (!nome.isEmpty()) {
            AppDatabase db = AppDatabase.getDatabase(this);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                com.example.falla.usuario.Usuario usuario = db.usuarioDao().getUsuario();
                if (usuario == null) usuario = new com.example.falla.usuario.Usuario();
                usuario.nome = nome;
                db.usuarioDao().salvar(usuario);
            });
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    // ── Adapter interno ──────────────────────────────────────────────────
    private class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

        @NonNull @Override
        public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_slide_onboarding, parent, false);
            return new SlideViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
            holder.icone.setImageResource(icones[position]);
            holder.titulo.setText(titulos[position]);
            holder.descricao.setText(descricoes[position]);

            View containerNome = holder.itemView.findViewById(R.id.container_campo_nome);
            android.widget.EditText edtNome = holder.itemView.findViewById(R.id.edt_nome_onboarding);

            if (position == 1) {
                containerNome.setVisibility(View.VISIBLE);
                edtNomeSlide = edtNome; // guarda referência para salvar depois
            } else {
                containerNome.setVisibility(View.GONE);
            }
        }

        @Override public int getItemCount() { return titulos.length; }

        class SlideViewHolder extends RecyclerView.ViewHolder {
            ImageView icone;
            TextView titulo, descricao;
            SlideViewHolder(@NonNull View v) {
                super(v);
                icone    = v.findViewById(R.id.img_slide_icone);
                titulo   = v.findViewById(R.id.txt_slide_titulo);
                descricao = v.findViewById(R.id.txt_slide_descricao);
            }
        }
    }
}