package com.example.falla.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class AssetImageHelper {

    public static void carregarImagem(Context context, String caminho, ImageView imageView) {
        if (caminho == null || caminho.isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_help);
            return;
        }

        // URI da galeria
        if (caminho.startsWith("content://")) {
            try {
                imageView.setImageURI(android.net.Uri.parse(caminho));
            } catch (Exception e) {
                imageView.setImageResource(android.R.drawable.ic_menu_help);
            }
            return;
        }

        // Ícone do Android
        if (caminho.matches("\\d+")) {
            try {
                imageView.setImageResource(Integer.parseInt(caminho));
            } catch (Exception e) {
                imageView.setImageResource(android.R.drawable.ic_menu_help);
            }
            return;
        }

        // Imagem dos assets
        if (caminho.startsWith("assets/")) {
            try {
                String caminhoRelativo = caminho.replace("assets/", "");
                InputStream is = context.getAssets().open(caminhoRelativo);
                Drawable drawable = Drawable.createFromStream(is, null);
                imageView.setImageDrawable(drawable);
                is.close();
            } catch (Exception e) {
                Log.e("AssetImageHelper", "Imagem não encontrada: " + caminho);
                imageView.setImageResource(android.R.drawable.ic_menu_help);
            }
            return;
        }

        // Fallback
        imageView.setImageResource(android.R.drawable.ic_menu_help);
    }
}