package com.example.falla.DAO;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.falla.card.Converters;
import com.example.falla.card.ItemCard;
import com.example.falla.card.ItemCardDao;
import com.example.falla.usuario.Usuario;
import com.example.falla.usuario.UsuarioDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters({Converters.class}) // Adicione esta linha
@Database(entities = {Usuario.class, ItemCard.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract ItemCardDao itemCardDao(); // Adicione isso

    private static volatile AppDatabase INSTANCE;
    // Executor com 4 threads para operações de banco
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "banco-falla")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}