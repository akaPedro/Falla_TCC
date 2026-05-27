package com.example.falla.card;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "itens_cards")
public class ItemCard {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String texto;
    private String fala;
    private String imagemUri;
    private CategoriaItem categoria;
    private boolean isFavorito;

    // --- CONSTRUTOR PADRÃO  ---
    public ItemCard() {}

    // --- CONSTRUTOR CORRETO PARA INSERÇÃO ---
    public ItemCard(String fala, CategoriaItem categoria, String imagemUri) {
        this.fala = fala;
        this.texto = fala;
        this.categoria = categoria;
        this.imagemUri = imagemUri;
        this.isFavorito = false; // Todo card novo nasce sem estrela
    }



    // Construtor especial — texto exibido diferente da fala (números, letras, cores, formas)
    public ItemCard(String texto, CategoriaItem categoria, String imagemUri, String fala) {
        this.texto = texto;
        this.fala = fala;
        this.categoria = categoria;
        this.imagemUri = imagemUri;
        this.isFavorito = false;
    }

    // --- ADICIONE ESSES GETTERS E SETTERS ABAIXO ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getFala() { return fala; }
    public void setFala(String fala) { this.fala = fala; }

    public String getImagemUri() { return imagemUri; }
    public void setImagemUri(String imagemUri) { this.imagemUri = imagemUri; }

    public CategoriaItem getCategoria() { return categoria; }
    public void setCategoria(CategoriaItem categoria) { this.categoria = categoria; }

    public boolean isFavorito() { return isFavorito; }
    public void setFavorito(boolean favorito) { isFavorito = favorito; }
}