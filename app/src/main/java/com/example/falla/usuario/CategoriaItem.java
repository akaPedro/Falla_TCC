package com.example.falla.usuario;

public enum CategoriaItem {
    FAVORITOS("Favoritos"),
    PESSOAL("Pessoal"),
    COMIDAS("Comidas");

    private final String nome;

    CategoriaItem(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
