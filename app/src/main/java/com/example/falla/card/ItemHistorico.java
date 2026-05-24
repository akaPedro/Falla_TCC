package com.example.falla.card; // Ajuste o pacote se necessário

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_historico")
public class ItemHistorico {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String data;
    public String hora;
    public String imagemUri;
    public String fala;

    public ItemHistorico(String data, String hora, String imagemUri, String fala) {
        this.data = data;
        this.hora = hora;
        this.imagemUri = imagemUri;
        this.fala = fala;
    }
}