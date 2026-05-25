package com.example.falla.historico; // Ajuste o pacote se necessário

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.falla.card.ItemHistorico;
import java.util.List;

@Dao
public interface HistoricoDao {
    @Insert
    void inserir(ItemHistorico historico);

    // Busca ordenando do mais recente (maior ID) para o mais antigo
    @Query("SELECT * FROM tabela_historico ORDER BY id DESC LIMIT 100")
    List<ItemHistorico> buscarTodoHistorico();
}