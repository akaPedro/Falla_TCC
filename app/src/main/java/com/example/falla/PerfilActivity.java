package com.example.falla;

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
import androidx.room.Room;

import com.example.falla.DAO.AppDatabase;

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

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Inicializando os componentes
        voltar = findViewById(R.id.btn_voltar);
        edtNome = findViewById(R.id.edt_nome);
        edtRegistro = findViewById(R.id.edt_registro);
        btnMenino = findViewById(R.id.btn_menino);
        btnMenina = findViewById(R.id.btn_menina);
        fotoPerfil = findViewById(R.id.img_foto_perfil);
        editFoto = findViewById(R.id.edit_foto);

        db = AppDatabase.getDatabase(this);

        // CARREGAR DADOS (Thread de Background)
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Usuario user = db.usuarioDao().getUsuario();

            // Se o usuário existir, voltamos para a Main Thread para atualizar a UI
            if (user != null) {
                runOnUiThread(() -> {
                    edtNome.setText(user.nome);
                    edtRegistro.setText(user.registro);
                    caminhoFotoAtual = user.caminhoFoto;
                    if (caminhoFotoAtual != null) {
                        fotoPerfil.setImageURI(Uri.fromFile(new File(caminhoFotoAtual)));
                    }
                });
            }
        });

        // Lógica de seleção de Gênero
        btnMenino.setOnClickListener(v -> selecionarGenero("menino"));
        btnMenina.setOnClickListener(v -> selecionarGenero("menina"));

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                // 1. Limpa o tint para a foto aparecer colorida
                fotoPerfil.setImageTintList(null);

                // 2. Mostra na tela imediatamente
                fotoPerfil.setImageURI(uri);

                // 3. Copia para a pasta interna e guarda o caminho para o Room
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    String caminhoLocal = copiarImagemParaInterno(uri);
                    if (caminhoLocal != null) {
                        this.caminhoFotoAtual = caminhoLocal;
                        runOnUiThread(() -> fotoPerfil.setImageURI(Uri.fromFile(new File(caminhoLocal)))); //?
                    }
                });
            }
        });

        // 2. Gatilho para abrir a galeria
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

        // LÓGICA DE SALVAR AO SAIR
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                salvarDadosERetornar();
            }
        });

        voltar.setOnClickListener(v -> salvarDadosERetornar());
    }

    private void salvarDadosERetornar() {
        String nome = edtNome.getText().toString();
        String registro = edtRegistro.getText().toString();

        // SALVAR DADOS (Thread de Background)
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Usuario usuario = new Usuario();
            usuario.nome = nome;
            usuario.registro = registro;
            usuario.caminhoFoto = caminhoFotoAtual;
            // usuario.genero = ... (pegue a variável de gênero aqui)

            db.usuarioDao().salvar(usuario);

            // Após salvar, finaliza a activity na Main Thread
            runOnUiThread(() -> finish());
        });
    }

    private void selecionarGenero(String genero) {
        if (genero.equals("menino")) {
            // Destaca Menino, apaga Menina (Exemplo simples trocando a cor do texto)
            btnMenino.setTextColor(ContextCompat.getColor(this, R.color.white)); // Ou uma cor de destaque
            btnMenina.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            Toast.makeText(this, "Gênero: Menino", Toast.LENGTH_SHORT).show();
        } else {
            btnMenina.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnMenino.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            Toast.makeText(this, "Gênero: Menina", Toast.LENGTH_SHORT).show();
        }
    }

    private String copiarImagemParaInterno(Uri uri) {
        try {
            // Cria o arquivo dentro de: /data/user/0/com.example.falla/files/perfil.jpg
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

            return arquivoDestino.getAbsolutePath(); // Este é o caminho que vai para o Room
        } catch (IOException e) {
            Log.e("ERRO_SALVAR", "Falha ao copiar imagem", e);
            return null;
        }
    }

}