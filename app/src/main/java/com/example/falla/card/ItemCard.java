package com.example.falla.card;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "itens_cards")
public class ItemCard {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private CategoriaItem categoria;
    private String texto;
    private String imagemPath; // Caminho da imagem
    private boolean isFavorito;

    // 1. Construtor vazio (Obrigatório para o Room)
    public ItemCard() {
    }

    // 2. Construtor personalizado (O @Ignore diz para o Room não usar este)
    @Ignore
    public ItemCard(String texto, String imagemPath, CategoriaItem categoria, boolean isFavorito) {
        this.texto = texto;
        this.imagemPath = imagemPath;
        this.categoria = categoria;
        this.isFavorito = isFavorito;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }

    public CategoriaItem getCategoria() { return categoria; }
    public void setCategoria(CategoriaItem categoria) { this.categoria = categoria; }

    public boolean isFavorito() { return isFavorito; }
    public void setFavorito(boolean favorito) { isFavorito = favorito; }
}