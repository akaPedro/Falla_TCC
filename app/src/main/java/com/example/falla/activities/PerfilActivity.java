package com.example.falla.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.falla.DAO.AppDatabase;
import com.example.falla.R;
import com.example.falla.usuario.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PerfilActivity extends AppCompatActivity {

    private ImageView voltar;
    private EditText edtNome, edtRegistro;
    private TextView btnMenino, btnMenina;
    private ImageView fotoPerfil;
    private CardView editFoto;
    private AppDatabase db;
    private String caminhoFotoAtual = null;
    private String generoSelecionado = null;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        voltar = findViewById(R.id.btn_voltar);
        edtNome = findViewById(R.id.edt_nome);
        edtRegistro = findViewById(R.id.edt_registro);
        btnMenino = findViewById(R.id.btn_menino);
        btnMenina = findViewById(R.id.btn_menina);
        fotoPerfil = findViewById(R.id.img_foto_perfil);
        editFoto = findViewById(R.id.edit_foto);

        db = AppDatabase.getDatabase(this);

        // CARREGAR DADOS DO BANCO
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Usuario user = db.usuarioDao().getUsuario();

            if (user != null) {
                runOnUiThread(() -> {
                    edtNome.setText(user.nome);
                    edtRegistro.setText(user.registro);
                    caminhoFotoAtual = user.caminhoFoto;

                    if (caminhoFotoAtual != null) {
                        fotoPerfil.setImageURI(Uri.fromFile(new File(caminhoFotoAtual)));
                    }

                    // ✅ ADICIONADO: restaura o gênero salvo e destaca o botão correto
                    if (user.genero != null) {
                        generoSelecionado = user.genero;
                        atualizarBotoesGenero(user.genero);
                    }
                });
            }
        });

        // Lógica de seleção de Gênero
        btnMenino.setOnClickListener(v -> selecionarGenero("menino"));
        btnMenina.setOnClickListener(v -> selecionarGenero("menina"));

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                fotoPerfil.setImageTintList(null);
                fotoPerfil.setImageURI(uri);

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    String caminhoLocal = copiarImagemParaInterno(uri);
                    if (caminhoLocal != null) {
                        this.caminhoFotoAtual = caminhoLocal;
                        runOnUiThread(() -> fotoPerfil.setImageURI(Uri.fromFile(new File(caminhoLocal))));
                    }
                });
            }
        });

        editFoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                salvarDadosERetornar();
            }
        });

        voltar.setOnClickListener(v -> salvarDadosERetornar());
    }

    private void selecionarGenero(String genero) {
        generoSelecionado = genero; // ✅ ADICIONADO: salva na variável
        atualizarBotoesGenero(genero);
    }

    // ✅ ADICIONADO: método separado para destacar os botões (usado no carregamento e na seleção)
    private void atualizarBotoesGenero(String genero) {
        if (genero.equals("menino")) {
            btnMenino.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnMenino.setAlpha(1.0f);
            btnMenina.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnMenina.setAlpha(0.45f); // ← apaga o não selecionado
        } else {
            btnMenina.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnMenina.setAlpha(1.0f);
            btnMenino.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnMenino.setAlpha(0.45f);
        }
    }

    private void salvarDadosERetornar() {
        String nome = edtNome.getText().toString();
        String registro = edtRegistro.getText().toString();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Usuario usuario = new Usuario();
            usuario.nome = nome;
            usuario.registro = registro;
            usuario.caminhoFoto = caminhoFotoAtual;
            usuario.genero = generoSelecionado; // ✅ ADICIONADO: salva o gênero

            db.usuarioDao().salvar(usuario);
            runOnUiThread(() -> finish());
        });
    }

    private String copiarImagemParaInterno(Uri uri) {
        try {
            File arquivoDestino = new File(getFilesDir(), "foto_perfil.jpg");
            InputStream in = getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(arquivoDestino);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
            return arquivoDestino.getAbsolutePath();
        } catch (IOException e) {
            Log.e("ERRO_SALVAR", "Falha ao copiar imagem", e);
            return null;
        }
    }
}