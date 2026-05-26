package com.example.falla.card;

public enum CategoriaItem {
    // Categorias principais
    FAVORITOS("Favoritos"),
    PESSOAL("Pessoal"),
    COMIDAS("Comidas"),
    LAZER("Lazer"),
    APRENDIZADO("Aprendizado"),

    // Subgavetas: Pessoal
    PESSOAL_EU("Eu"),
    PESSOAL_SENTIMENTOS("Sentimentos"),
    PESSOAL_CUIDADOS("Cuidados"),
    PESSOAL_ROUPAS("Roupas"),
    PESSOAL_ACOES("Ações"),
    PESSOAL_REFERENCIA("Referência"),

    // Subgavetas: Comidas
    COMIDAS_REFEICAO("Refeição"),
    COMIDAS_CAFE_LANCHES("Café e Lanches"),
    COMIDAS_BEBIDAS("Bebidas"),
    COMIDAS_DOCES("Doces e Sobremesas"),

    // Subgavetas: Lazer
    LAZER_JOGOS("Jogos e Brinquedos"),
    LAZER_TELAS("Telas e Mídias"),
    LAZER_EXTERNO("Atividades Externas"),
    LAZER_SOCIAL("Interação Social"),

    // Subgavetas: Aprendizado
    APRENDIZADO_NUMEROS("Números"),
    APRENDIZADO_ALFABETO("Alfabeto"),
    APRENDIZADO_VOGAIS("Vogais"),
    APRENDIZADO_CORES("Cores"),
    APRENDIZADO_FORMAS("Formas");

    private final String nome;

    CategoriaItem(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}