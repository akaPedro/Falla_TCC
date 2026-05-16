package com.example.falla.card;

public enum CategoriaItem {
    FAVORITOS("Favoritos"),
    PESSOAL("Pessoal"),
    COMIDAS("Comidas"),
    LAZER("Lazer"),
    REFERENCIA("Referencia"),
    APRENDIZADO("Aprendizado");


    private final String nome;

    CategoriaItem(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
