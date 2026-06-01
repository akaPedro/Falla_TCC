package com.example.falla.card; // Altere para o seu pacote

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cards")
public class CardEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fala;
    public String imagemUri;
    public String categoria;
    public boolean isFavorito;

    // Construtor vazio para o Room
    public CardEntity() {}

    // Construtor prático para criar novos cards
    public CardEntity(String fala, String imagemUri, String categoria, boolean isFavorito) {
        this.fala = fala;
        this.imagemUri = imagemUri;
        this.categoria = categoria;
        this.isFavorito = isFavorito;
    }
}