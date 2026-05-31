package com.example.falla.historico;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.falla.DAO.AppDatabase;
import com.example.falla.R;
import com.example.falla.card.AssetImageHelper;
import com.example.falla.card.ItemHistorico;

import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout containerListaHistorico;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);

        findViewById(R.id.btn_voltar_historico).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_historico), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão de voltar padrão
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        containerListaHistorico = findViewById(R.id.container_lista_historico);
        db = AppDatabase.getDatabase(this);

        carregarHistoricoDaBase();
    }

    private void carregarHistoricoDaBase() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Puxa do banco do mais recente para o mais antigo
            List<ItemHistorico> listaHistorico = db.historicoDao().buscarTodoHistorico();

            // Desenha na tela (precisa ser na Thread Principal)
            runOnUiThread(() -> {
                containerListaHistorico.removeAllViews();

                if (listaHistorico == null || listaHistorico.isEmpty()) {
                    return; // Histórico vazio, não faz nada
                }

                for (ItemHistorico item : listaHistorico) {
                    // Infla o "molde" que criamos (item_historico.xml)
                    View viewItem = getLayoutInflater().inflate(R.layout.item_historico, containerListaHistorico, false);

                    TextView txtHora = viewItem.findViewById(R.id.txt_hist_hora);
                    TextView txtData = viewItem.findViewById(R.id.txt_hist_data);
                    ImageView imgSimbolo = viewItem.findViewById(R.id.img_hist_simbolo);
                    TextView txtFala = viewItem.findViewById(R.id.txt_hist_fala);

                    // Preenche com os dados do banco
                    txtHora.setText(item.hora);
                    txtData.setText(item.data);
                    txtFala.setText(item.fala); // <- ADICIONAR




                    // Lógica para carregar a imagem (Idêntica à da MainActivity)
                    AssetImageHelper.carregarImagem(this, item.imagemUri, imgSimbolo);

                    // Adiciona a linha na tela
                    containerListaHistorico.addView(viewItem);
                }
            });
        });
    }



}