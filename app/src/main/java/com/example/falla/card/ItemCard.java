package com.example.falla.card;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "itens_cards")
public class ItemCard {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fala;
    private String imagemUri;
    private CategoriaItem categoria;
    private boolean isFavorito;

    // --- CONSTRUTOR PADRÃO ---
    public ItemCard() {}

    // --- ADICIONE ESSES GETTERS E SETTERS ABAIXO ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFala() { return fala; }
    public void setFala(String fala) { this.fala = fala; }

    public String getImagemUri() { return imagemUri; }
    public void setImagemUri(String imagemUri) { this.imagemUri = imagemUri; }

    public CategoriaItem getCategoria() { return categoria; }
    public void setCategoria(CategoriaItem categoria) { this.categoria = categoria; }

    public boolean isFavorito() { return isFavorito; }
    public void setFavorito(boolean favorito) { isFavorito = favorito; }
}