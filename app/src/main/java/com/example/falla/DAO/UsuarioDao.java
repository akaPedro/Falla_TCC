package com.example.falla.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.falla.Usuario;

@Dao
public interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void salvar(Usuario usuario);

    @Query("SELECT * FROM perfil_usuario WHERE id = 1")
    Usuario getUsuario();
}
