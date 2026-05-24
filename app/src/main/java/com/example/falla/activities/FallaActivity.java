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
                falarTexto(campoTexto.getText().toString());
            }
        });

        // Gatilho para abrir o Dialog de Criação
        btnAbrirMenuCriacao.setOnClickListener(v -> {
            String textoAtual = campoTexto.getText().toString().trim();
            abrirDialogCriarCard(textoAtual);
        });

        voltaria.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

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

    private void abrirDialogCriarCard(String textoInicial) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_criar_card, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        // Fundo transparente para respeitar as bordas arredondadas do CardView no XML
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Elementos do Dialog
        FrameLayout containerImagem = dialogView.findViewById(R.id.container_imagem_novo);
        imgPreviewDialogAtual = dialogView.findViewById(R.id.img_preview_novo);
        EditText editTextoNovo = dialogView.findViewById(R.id.edit_texto_novo);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinner_categoria_novo);
        TextView btnCancelar = dialogView.findViewById(R.id.btn_cancelar_novo);
        TextView btnSalvar = dialogView.findViewById(R.id.btn_salvar_novo);

        // Reset da imagem (caso queira usar um ícone padrão como string)
        uriImagemTemporaria = String.valueOf(android.R.drawable.ic_menu_camera);

        // Preenche com o texto que o usuário já tinha digitado fora do menu
        editTextoNovo.setText(textoInicial);

        // Popula o Spinner com as categorias
        ArrayAdapter<CategoriaItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CategoriaItem.values());
        spinnerCategoria.setAdapter(adapter);

        // Abrir galeria
        containerImagem.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Ações dos botões
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String textoFinal = editTextoNovo.getText().toString().trim();

            if (textoFinal.isEmpty()) {
                Toast.makeText(this, "O texto é obrigatório!", Toast.LENGTH_SHORT).show();
                return;
            }

            CategoriaItem catSelecionada = (CategoriaItem) spinnerCategoria.getSelectedItem();
            ItemCard novoCard = new ItemCard(textoFinal, catSelecionada, uriImagemTemporaria);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.itemCardDao().inserir(novoCard);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Card criado e salvo no banco!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish(); // Retorna para a tela principal
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}