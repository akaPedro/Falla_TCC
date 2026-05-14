package com.example.falla.usuario;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemCardDao {
    @Insert
    void inserir(ItemCard item);

    // Mude de String para CategoriaItem aqui
    @Query("SELECT * FROM itens_cards WHERE categoria = :cat")
    List<ItemCard> buscarPorCategoria(CategoriaItem cat);

    @Query("SELECT * FROM itens_cards WHERE isFavorito = 1")
    List<ItemCard> buscarFavoritos();

    @Update
    void atualizar(ItemCard item);

    @Delete
    void deletar(ItemCard item);
}