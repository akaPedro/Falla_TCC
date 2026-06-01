package com.example.falla.activities;

import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.falla.DAO.AppDatabase;
import com.example.falla.R;
import com.example.falla.card.CategoriaItem;
import com.example.falla.card.ItemCard;

import java.util.Locale;

public class FallaActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageView voltaria;
    private View btnFallar;
    private androidx.cardview.widget.CardView btnAbrirMenuCriacao;
    private EditText campoTexto;
    private TextToSpeech tts;
    private AppDatabase db;

    // Variáveis para a imagem temporária do Dialog
    private ImageView imgPreviewDialogAtual;
    private String uriImagemTemporaria = null;

    // Lançador para abrir a galeria
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && imgPreviewDialogAtual != null) {
                    uriImagemTemporaria = uri.toString();
                    imgPreviewDialogAtual.setImageURI(uri);
                    getContentResolver().takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_falla);

        db = AppDatabase.getDatabase(this);

        voltaria = findViewById(R.id.btn_voltar_ia);
        btnFallar = findViewById(R.id.btn_falar);
        btnAbrirMenuCriacao = findViewById(R.id.btn_favoritar_ia);
        campoTexto = findViewById(R.id.edt_fala_ia);
        tts = new TextToSpeech(this, this);

        // Gatilho para Falar
        btnFallar.setOnClickListener(v -> {
            if (campoTexto != null && campoTexto.getText() != null) {
                String texto = campoTexto.getText().toString().trim();
                if (!texto.isEmpty()) {
                    falarTexto(texto);
                    registrarNoHistorico(texto);
                }
            }
        });

        // Gatilho para abrir o Dialog de Criação
        btnAbrirMenuCriacao.setOnClickListener(v -> {
            String textoAtual = campoTexto.getText().toString().trim();
            abrirDialogCriarCard(textoAtual);
        });

        voltaria.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        findViewById(R.id.btn_ajuda).setOnClickListener(v -> abrirDialogAjuda());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.falla), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão voltar padrão
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void abrirDialogCriarCard(String textoInicial) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_criar_card, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        FrameLayout containerImagem   = dialogView.findViewById(R.id.container_imagem_novo);
        imgPreviewDialogAtual         = dialogView.findViewById(R.id.img_preview_novo);
        EditText editTextoNovo        = dialogView.findViewById(R.id.edit_texto_novo);
        Spinner spinnerCategoria      = dialogView.findViewById(R.id.spinner_categoria_novo);
        Spinner spinnerSub            = dialogView.findViewById(R.id.spinner_subcategoria_novo);
        View containerSub             = dialogView.findViewById(R.id.container_spinner_sub);
        TextView btnCancelar          = dialogView.findViewById(R.id.btn_cancelar_novo);
        TextView btnSalvar            = dialogView.findViewById(R.id.btn_salvar_novo);

        uriImagemTemporaria = String.valueOf(android.R.drawable.ic_menu_camera);
        editTextoNovo.setText(textoInicial);

        // Mapeamento: categoria principal > suas subcategorias
        java.util.Map<CategoriaItem, CategoriaItem[]> subMap = new java.util.LinkedHashMap<>();
        subMap.put(CategoriaItem.PESSOAL, new CategoriaItem[]{
                CategoriaItem.PESSOAL,
                CategoriaItem.PESSOAL_EU,
                CategoriaItem.PESSOAL_SAUDE,
                CategoriaItem.PESSOAL_CUIDADOS,
                CategoriaItem.PESSOAL_ROUPAS,
                CategoriaItem.PESSOAL_ACOES,
                CategoriaItem.PESSOAL_REFERENCIA
        });
        subMap.put(CategoriaItem.COMIDAS, new CategoriaItem[]{
                CategoriaItem.COMIDAS,
                CategoriaItem.COMIDAS_REFEICAO,
                CategoriaItem.COMIDAS_CAFE_LANCHES,
                CategoriaItem.COMIDAS_BEBIDAS,
                CategoriaItem.COMIDAS_DOCES
        });
        subMap.put(CategoriaItem.LAZER, new CategoriaItem[]{
                CategoriaItem.LAZER,
                CategoriaItem.LAZER_JOGOS,
                CategoriaItem.LAZER_TELAS,
                CategoriaItem.LAZER_EXTERNO,
                CategoriaItem.LAZER_SOCIAL
        });
        subMap.put(CategoriaItem.APRENDIZADO, new CategoriaItem[]{
                CategoriaItem.APRENDIZADO,
                CategoriaItem.APRENDIZADO_NUMEROS,
                CategoriaItem.APRENDIZADO_ALFABETO,
                CategoriaItem.APRENDIZADO_VOGAIS,
                CategoriaItem.APRENDIZADO_CORES,
                CategoriaItem.APRENDIZADO_FORMAS
        });

        // Spinner de categorias principais
        CategoriaItem[] principais = {
                CategoriaItem.FAVORITOS,
                CategoriaItem.PESSOAL,
                CategoriaItem.COMIDAS,
                CategoriaItem.LAZER,
                CategoriaItem.APRENDIZADO
        };

        // Adapter que exibe o nome legível
        android.widget.ArrayAdapter<CategoriaItem> adapterPrincipal =
                new android.widget.ArrayAdapter<CategoriaItem>(this,
                        android.R.layout.simple_spinner_dropdown_item, principais) {
                    @Override public String toString() { return ""; }

                    @androidx.annotation.NonNull
                    @Override
                    public android.view.View getView(int pos, android.view.View v, @androidx.annotation.NonNull android.view.ViewGroup parent) {
                        android.widget.TextView tv = (android.widget.TextView)
                                super.getView(pos, v, parent);
                        tv.setText(principais[pos].getNome());
                        return tv;
                    }

                    @Override
                    public android.view.View getDropDownView(int pos, android.view.View v, @androidx.annotation.NonNull android.view.ViewGroup parent) {
                        android.widget.TextView tv = (android.widget.TextView)
                                super.getDropDownView(pos, v, parent);
                        tv.setText(principais[pos].getNome());
                        return tv;
                    }
                };
        spinnerCategoria.setAdapter(adapterPrincipal);

        // Quando muda a categoria principal, atualiza o spinner de subcategorias
        spinnerCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                CategoriaItem catSelecionada = principais[pos];
                CategoriaItem[] subs = subMap.get(catSelecionada);

                if (subs != null && subs.length > 0) {
                    // Tem subcategorias: mostra o segundo spinner
                    android.widget.ArrayAdapter<CategoriaItem> adapterSub =
                            new android.widget.ArrayAdapter<CategoriaItem>(FallaActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, subs) {
                                @androidx.annotation.NonNull
                                @Override
                                public android.view.View getView(int p, android.view.View v, @androidx.annotation.NonNull android.view.ViewGroup par) {
                                    android.widget.TextView tv = (android.widget.TextView)
                                            super.getView(p, v, par);
                                    tv.setText(subs[p].getNome());
                                    return tv;
                                }
                                @Override
                                public android.view.View getDropDownView(int p, android.view.View v, @androidx.annotation.NonNull android.view.ViewGroup par) {
                                    android.widget.TextView tv = (android.widget.TextView)
                                            super.getDropDownView(p, v, par);
                                    tv.setText(subs[p].getNome());
                                    return tv;
                                }
                            };
                    spinnerSub.setAdapter(adapterSub);
                    containerSub.setVisibility(View.VISIBLE);
                } else {
                    // Favorito não tem subcategoria: esconde o segundo spinner
                    containerSub.setVisibility(View.GONE);
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        containerImagem.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String textoFinal = editTextoNovo.getText().toString().trim();
            if (textoFinal.isEmpty()) {
                Toast.makeText(this, "O texto é obrigatório!", Toast.LENGTH_SHORT).show();
                return;
            }

            CategoriaItem catFinal;
            boolean deveSerFavorito = false;

            if (containerSub.getVisibility() == View.VISIBLE) {
                CategoriaItem subSelecionada = (CategoriaItem) spinnerSub.getSelectedItem();

                // se selecionou "Nenhuma", vai para o grid coringa da categoria pai
                if (subSelecionada == null || subSelecionada.name().equals("NENHUMA")) {
                    catFinal = (CategoriaItem) spinnerCategoria.getSelectedItem();
                } else {
                    catFinal = subSelecionada;
                }
            } else {
                catFinal = (CategoriaItem) spinnerCategoria.getSelectedItem();
            }

            // card criado na categoria favorito já nasce com estrela ligada
            if (catFinal == CategoriaItem.FAVORITOS) {
                deveSerFavorito = true;
            }

            final boolean favorito = deveSerFavorito;
            ItemCard novoCard = new ItemCard(textoFinal, catFinal, uriImagemTemporaria);
            novoCard.setFavorito(favorito);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.itemCardDao().inserir(novoCard);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Card criado!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                });
            });
        });
        dialog.show();
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("pt", "BR"));
        }
    }

    public void falarTexto(String texto) {
        if (tts != null && !texto.trim().isEmpty()) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "falla_id_" + System.currentTimeMillis());
        }
    }

    // registra a digitação livre no histórico
    private void registrarNoHistorico(String texto) {
        java.text.SimpleDateFormat formataData = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        java.text.SimpleDateFormat formataHora = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        java.util.Date agora = new java.util.Date();

        com.example.falla.card.ItemHistorico novoRegistro = new com.example.falla.card.ItemHistorico(
                formataData.format(agora),
                formataHora.format(agora),
                String.valueOf(android.R.drawable.ic_menu_edit),
                texto
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (db != null) db.historicoDao().inserir(novoRegistro);
        });
    }

    private void abrirDialogAjuda() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ajuda_falla, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.findViewById(R.id.btn_fechar_ajuda).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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