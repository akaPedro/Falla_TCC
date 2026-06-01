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

    // Construtor padrão para o Room
    public ItemCard() {}

    // Construtor para inserção
    public ItemCard(String fala, CategoriaItem categoria, String imagemUri) {
        this.fala = fala;
        this.texto = fala;
        this.categoria = categoria;
        this.imagemUri = imagemUri;
        this.isFavorito = false;
    }



    // Construtor especial, texto exibido diferente da fala
    public ItemCard(String texto, CategoriaItem categoria, String imagemUri, String fala) {
        this.texto = texto;
        this.fala = fala;
        this.categoria = categoria;
        this.imagemUri = imagemUri;
        this.isFavorito = false;
    }

    // Getter e Setter

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