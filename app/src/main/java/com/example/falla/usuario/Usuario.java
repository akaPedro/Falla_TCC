package com.example.falla.usuario;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "perfil_usuario")
public class Usuario {
    @PrimaryKey
    public int id = 1; // Só teremos um perfil, então o ID é sempre 1

    public String nome;
    public String registro;
    public String genero;
    public String caminhoFoto; // O caminho que salvamos internamente
}
