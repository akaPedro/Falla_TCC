package com.example.falla.usuario;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static String fromCategoria(CategoriaItem categoria) {
        return categoria == null ? null : categoria.name();
    }

    @TypeConverter
    public static CategoriaItem toCategoria(String value) {
        return value == null ? null : CategoriaItem.valueOf(value);
    }
}