package com.example.falla.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.falla.DAO.AppDatabase;
import com.example.falla.R;
import com.example.falla.card.CardEntity;
import com.example.falla.card.CategoriaItem;
import com.example.falla.card.ItemCard;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private ImageView ImgFll;
    private ImageView ImgPerf;
    private DrawerLayout drawerLayout;
    private AppCompatImageView imgMenu;
    // Itens da barra lateral
    private TextView itemTamanho, itemCores, itemHistorico, itemSobre, itemGrande, itemPequeno, btnSalvarEdt;
    private LinearLayout headerPessoal, headerFavoritos, headerComidas, headerLazer, headerReferencia, headerAprendizado, submenuTamanho;
    private ImageView setaPessoal, setaFavoritos, setaComidas, setaLazer, setaReferencia, setaAprendizado;
    private GridLayout conteudoPessoal, conteudoFavoritos, conteudoComidas, conteudoLazer, conteudoReferencia, conteudoAprendizado;
    // Variável temporária para guardar em qual ImageView vamos colocar a foto da galeria
    private ImageView imagemEmEdicaoAtual = null;

    private AppDatabase db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // #####  BANCO DE DADOS  ##### //
        db = AppDatabase.getDatabase(MainActivity.this);
        // Carrega os cards do banco em segundo plano
        verificarEPreencherBancoInicial();

       // #####  MAIN  ##### //
        drawerLayout = findViewById(R.id.main);

        // #####  FALAR  ##### //
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Define o idioma para Português Brasil
                int result = tts.setLanguage(new Locale("pt", "BR"));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Idioma não suportado ou faltando dados.");
                }
            } else {
                Log.e("TTS", "Falha na inicialização!");
            }
        });

        // #####  BARRA PRINCIPAL  ##### //
        ImgFll = findViewById(R.id.img_keyboard);
        ImgPerf = findViewById(R.id.img_profile);


        // #####  BARRA LATERAL  ##### //
        imgMenu = findViewById(R.id.img_menu);

        itemTamanho = findViewById(R.id.item_tamanho);
            // #######  TAMANHO  ##### //
            submenuTamanho = findViewById(R.id.submenu_tamanho);
            itemGrande = findViewById(R.id.item_tamanho_grande);
            itemPequeno = findViewById(R.id.item_tamanho_pequeno);

        itemCores = findViewById(R.id.item_cores);
        itemHistorico = findViewById(R.id.item_historico);
        itemSobre = findViewById(R.id.item_sobre);



        // #####  HEADERS ##### //
        headerPessoal = findViewById(R.id.header_pessoal);
        headerFavoritos = findViewById(R.id.header_favorito);
        headerComidas = findViewById(R.id.header_Alimentos);
        headerLazer = findViewById(R.id.header_lazer);
        headerReferencia = findViewById(R.id.header_referencia);
        headerAprendizado = findViewById(R.id.header_aprendizado);

        // #####  SETAS ##### //
        setaPessoal = findViewById(R.id.seta_pessoal);
        setaFavoritos = findViewById(R.id.seta_favorito);
        setaComidas = findViewById(R.id.seta_alimento);
        setaLazer = findViewById(R.id.seta_lazer);
        setaReferencia = findViewById(R.id.seta_referencia);
        setaAprendizado = findViewById(R.id.seta_aprendizado);

        // #####  CONTEUDO / GRIDS  ##### //
        conteudoFavoritos = findViewById(R.id.conteudo_favoritos);
        conteudoPessoal = findViewById(R.id.conteudo_pessoal);
        conteudoComidas = findViewById(R.id.conteudo_alimentos);
        conteudoLazer = findViewById(R.id.conteudo_lazer);
        conteudoReferencia = findViewById(R.id.conteudo_referencia);
        conteudoAprendizado = findViewById(R.id.conteudo_aprendizado);

        // #####  LOGICA DE TAMANHO  ##### //
        itemGrande.setOnClickListener(v -> {
            conteudoFavoritos.setColumnCount(2);
            conteudoPessoal.setColumnCount(2);
            conteudoComidas.setColumnCount(2);
            conteudoLazer.setColumnCount(2);
            conteudoReferencia.setColumnCount(2);
            conteudoAprendizado.setColumnCount(2);
        });

        itemPequeno.setOnClickListener(v -> {
            conteudoFavoritos.setColumnCount(3);
            conteudoPessoal.setColumnCount(3);
            conteudoComidas.setColumnCount(3);
            conteudoLazer.setColumnCount(3);
            conteudoReferencia.setColumnCount(3);
            conteudoAprendizado.setColumnCount(3);
        });

//        // #####  SALVAR EDICOES  ##### //
//        btnSalvarEdt = findViewById(R.id.btn_salvar_edt);





//        // ############################  botao teste ################################ //
//
//
//
//        // 1. Inflar o layout do card dentro do seu metodo de criação/iteração
//        View cardView = getLayoutInflater().inflate(R.layout.item_card, conteudoPessoal, false);
//
//        //cards iniciais
//        androidx.cardview.widget.CardView cardRoot = cardView.findViewById(R.id.card_root);
//        ImageView imgEstrela = cardView.findViewById(R.id.img_estrela_favorito);
//        ImageView imgSimbolo = cardView.findViewById(R.id.img_card_simbolo);
//        TextView txtFala = cardView.findViewById(R.id.txt_card_fala);
//
//        // 3. Definir os valores dinâmicos do card (Exemplo de teste)
//        txtFala.setText("Item 1");
//        // imgSimbolo.setImageResource(R.drawable.seu_icone); // Quando tiver imagens próprias
//
//        // Variavel de controle do estado de favorito desse card
//        final boolean[] isFavorito = {false};
//
//        // 4. LÓGICA DA ESTRELA (Clique para favoritar/desfavoritar dinamicamente)
//        imgEstrela.setOnClickListener(v -> {
//            isFavorito[0] = !isFavorito[0];
//
//            // Referência para a gaveta de favoritos (certifique-se de dar findViewById nela no seu onCreate)
//            GridLayout conteudoFavoritos = findViewById(R.id.conteudo_favoritos);
//
//            // Tag única para conseguirmos achar a cópia do card na gaveta de favoritos depois
//            // Usamos o próprio texto do card + "_fav" como identificador único
//            String tagFavorito = txtFala.getText().toString() + "_fav";
//
//            if (isFavorito[0]) {
//                // 1. Modifica a estrela do card original para Amarela
//                imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);
//
//                // 2. Infla uma CÓPIA exata do card para jogar nos Favoritos
//                View cardFavorito = getLayoutInflater().inflate(R.layout.item_card, conteudoFavoritos, false);
//                cardFavorito.setTag(tagFavorito); // Marca o card com a tag única
//
//                // 3. Configura os elementos internos da cópia
//                ImageView estrelaFav = cardFavorito.findViewById(R.id.img_estrela_favorito);
//                ImageView simboloFav = cardFavorito.findViewById(R.id.img_card_simbolo);
//                TextView txtFalaFav = cardFavorito.findViewById(R.id.txt_card_fala);
//
//                // Copia os dados do original
//                txtFalaFav.setText(txtFala.getText());
//                simboloFav.setImageDrawable(imgSimbolo.getDrawable());
//                estrelaFav.setImageResource(android.R.drawable.btn_star_big_on); // Já nasce amarela
//
//                // 4. Se o usuário clicar na estrela dentro dos Favoritos, desfavorita em ambos os lugares
//                estrelaFav.setOnClickListener(vFav -> {
//                    // Simula o clique no card original para disparar a remoção em cadeia
//                    imgEstrela.performClick();
//                });
//
//                // 5. Configura o clique simples de fala na cópia dos favoritos
//                cardFavorito.setOnClickListener(vFav -> {
//                    Toast.makeText(MainActivity.this, "Falando (Favorito): " + txtFalaFav.getText(), Toast.LENGTH_SHORT).show();
//                    // Seu TTS aqui
//                });
//
//                // 6. Adiciona a cópia física na gaveta de favoritos
//                conteudoFavoritos.addView(cardFavorito);
//
//                Toast.makeText(MainActivity.this, "Adicionado aos Favoritos", Toast.LENGTH_SHORT).show();
//
//            } else {
//                // 1. Volta a estrela do card original para o modo Vazio
//                imgEstrela.setImageResource(android.R.drawable.btn_star_big_off);
//
//                // 2. Procura pela cópia do card lá na gaveta de favoritos usando a Tag única
//                View cardParaRemover = conteudoFavoritos.findViewWithTag(tagFavorito);
//
//                // 3. Se encontrar a cópia lá, remove ela do layout
//                if (cardParaRemover != null) {
//                    conteudoFavoritos.removeView(cardParaRemover);
//                    Toast.makeText(MainActivity.this, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        // LOGICA DO CLIQUE SEGUNDADO
//        cardRoot.setOnLongClickListener(v -> {
//            // Cria um menu pop-up ancorado no próprio card
//            PopupMenu popup = new PopupMenu(MainActivity.this, cardRoot);
//
//            // Adiciona as opções dinamicamente
//            popup.getMenu().add(0, 1, 0, "Editar Imagem e Fala");
//            popup.getMenu().add(0, 2, 1, "Excluir Card");
//
//            // Trata o clique de cada opção do menu
//            popup.setOnMenuItemClickListener(item -> {
//                switch (item.getItemId()) {
//                    case 1:
//                        // 1. Cria a visualização do Dialog baseado no XML
//                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_card, null);
//
//                        // 2. Configura a janela do Dialog
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                        builder.setView(dialogView);
//                        android.app.AlertDialog dialog = builder.create();
//
//                        // Isso deixa o fundo do AlertDialog transparente para as bordas arredondadas do CardView aparecerem
//                        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                        // 3. Pega as referências dos itens dentro do Dialog
//                        FrameLayout containerImagem = dialogView.findViewById(R.id.container_editar_imagem);
//                        ImageView imgDialogPreview = dialogView.findViewById(R.id.img_dialog_preview);
//                        EditText editFala = dialogView.findViewById(R.id.edit_dialog_fala);
//                        TextView btnCancelar = dialogView.findViewById(R.id.btn_dialog_cancelar);
//                        TextView btnSalvar = dialogView.findViewById(R.id.btn_dialog_salvar);
//
//                        // 4. Preenche o Dialog com os dados ATUAIS do card
//                        editFala.setText(txtFala.getText().toString());
//                        imgDialogPreview.setImageDrawable(imgSimbolo.getDrawable());
//
//                        // 5. Clique para abrir a Galeria
//                        containerImagem.setOnClickListener(vClick -> {
//                            // Salva a referência de qual ImageView estamos editando agora
//                            imagemEmEdicaoAtual = imgDialogPreview;
//                            // Abre a galeria buscando apenas imagens
//                            abrirGaleria.launch("image/*");
//                        });
//
//                        // 6. Ação de Cancelar
//                        btnCancelar.setOnClickListener(vClick -> dialog.dismiss());
//
//                        // 7. Ação de Salvar
//                        btnSalvar.setOnClickListener(vClick -> {
//                            // Atualiza o texto do card principal
//                            txtFala.setText(editFala.getText().toString());
//
//                            // Atualiza a imagem do card principal com a nova imagem do dialog
//                            imgSimbolo.setImageDrawable(imgDialogPreview.getDrawable());
//
//                            // Se este card tiver uma cópia nos favoritos, seria ideal atualizar lá também,
//                            // mas por hora isso já resolve a edição na gaveta atual!
//
//                            Toast.makeText(MainActivity.this, "Card atualizado!", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        });
//
//                        // Mostra a janelinha na tela
//                        dialog.show();
//                        return true;
//
//                    case 2:
//                        // Remove do layout físico
//                        android.view.ViewGroup parent = (android.view.ViewGroup) cardView.getParent();
//                        if (parent != null) {
//                            parent.removeView(cardView);
//                        }
//
//                        // --- REMOVER DO BANCO ---
//                        // db.cardDao().excluir(cardObjeto);
//
//                        Toast.makeText(MainActivity.this, "Removido do banco", Toast.LENGTH_SHORT).show();
//                        return true;
//
//                    default:
//                        return false;
//                }
//            });
//
//            popup.show(); // Exibe o menu na tela
//            return true; // Retorna true para indicar que o evento de segurar foi consumido
//        });
//
//        // 6. LÓGICA DO CLIQUE SIMPLES (Para disparar a FALA por áudio)
//        cardRoot.setOnClickListener(v -> {
//            String textoParaFalar = txtFala.getText().toString().trim();
//
//            if (!textoParaFalar.isEmpty()) {
//                // Exibe o feedback visual que você já tinha colocado
//                Toast.makeText(MainActivity.this, "Falando: " + textoParaFalar, Toast.LENGTH_SHORT).show();
//
//                // Dispara a voz. O QUEUE_FLUSH serve para interromper uma fala anterior se o usuário clicar rápido em outro card
//                tts.speak(textoParaFalar, TextToSpeech.QUEUE_FLUSH, null, null);
//            }
//        });

        // Por fim, adiciona o card configurado na sua respectiva gaveta
        // conteudoPessoal.addView(cardView);


        // ############################ teste ################################ //


        // #### Barra latreral #### //
        itemTamanho.setOnClickListener(v -> {
            if (submenuTamanho.getVisibility() == View.GONE) {
                submenuTamanho.setVisibility(View.VISIBLE);
            } else {
                submenuTamanho.setVisibility(View.GONE);
            }
        });

        itemCores.setOnClickListener(v -> {
            // Lógica para cores
            drawerLayout.closeDrawers();
        });

        itemHistorico.setOnClickListener(v -> {
            // Lógica para histórico
            drawerLayout.closeDrawers();
        });

        itemSobre.setOnClickListener(v -> {
            // Abrir uma Activity ou Dialog de "Sobre"
            drawerLayout.closeDrawers();
        });


        // Botões de navegação
        imgMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        ImgFll.setOnClickListener(v -> {
            Intent fallintent = new Intent(MainActivity.this, FallaActivity.class);
            startActivity(fallintent);
        });

        ImgPerf.setOnClickListener(v -> {
            Intent perfintent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(perfintent);
        });





        // #####  cards principais ##### //

        // Card Sim
        LinearLayout headerCardSim = findViewById(R.id.header_card_sim);
        LinearLayout conteudoCardSim = findViewById(R.id.conteudo_card_sim);
        TextView txtSetaSim = findViewById(R.id.txt_seta_sim);
        ImageView iconSim = findViewById(R.id.img_sim_icon);

        // Card Não
        LinearLayout headerCardNao = findViewById(R.id.header_card_nao);
        LinearLayout conteudoCardNao = findViewById(R.id.conteudo_card_nao);
        TextView txtSetaNao = findViewById(R.id.txt_seta_nao);
        ImageView iconNao = findViewById(R.id.img_nao_icon);

        // Configurar as interações principais
        configurarGavetaInternaCard(headerCardSim, conteudoCardSim, txtSetaSim, ">", "v");
        configurarGavetaInternaCard(headerCardNao, conteudoCardNao, txtSetaNao, ">", "v");

        // Se clicar direto na imagem do V/X, fala a palavra principal
        iconSim.setOnClickListener(v -> falar("Sim"));
        iconNao.setOnClickListener(v -> falar("Não"));

        // Falas do Card Sim
        findViewById(R.id.item_sim_quero).setOnClickListener(v -> falar("Sim, eu quero"));
        findViewById(R.id.item_sim_gosto).setOnClickListener(v -> falar("Sim, eu gosto"));
        findViewById(R.id.item_sim_bom).setOnClickListener(v -> falar("Isso é bom"));

        // Falas do Card Não
        findViewById(R.id.item_nao_quero).setOnClickListener(v -> falar("Não, eu não quero"));
        findViewById(R.id.item_nao_gosto).setOnClickListener(v -> falar("Não, eu não gosto"));
        findViewById(R.id.item_nao_ruim).setOnClickListener(v -> falar("Isso é ruim"));








        // #####  HEADERS ##### //

        headerFavoritos.setOnClickListener(v -> {
            if (conteudoFavoritos.getVisibility() == View.GONE) {
                conteudoFavoritos.setVisibility(View.VISIBLE);
                setaFavoritos.setRotation(90f); // Gira a seta para baixo
            } else {
                conteudoFavoritos.setVisibility(View.GONE);
                setaFavoritos.setRotation(0f); // Seta volta para a direita
            }
        });

        headerPessoal.setOnClickListener(v -> {
            if (conteudoPessoal.getVisibility() == View.GONE) {
                conteudoPessoal.setVisibility(View.VISIBLE);
                setaPessoal.setRotation(90f); // Gira a seta para baixo
            } else {
                conteudoPessoal.setVisibility(View.GONE);
                setaPessoal.setRotation(0f); // Seta volta para a direita
            }
        });

        headerComidas.setOnClickListener(v -> {
            if (conteudoComidas.getVisibility() == View.GONE) {
                conteudoComidas.setVisibility(View.VISIBLE);
                setaComidas.setRotation(90f); // Gira a seta para baixo
                } else {
                    conteudoComidas.setVisibility(View.GONE);
                    setaComidas.setRotation(0f); // Seta volta para a direita
                }
        });

        headerLazer.setOnClickListener(v -> {
            if (conteudoLazer.getVisibility() == View.GONE) {
                conteudoLazer.setVisibility(View.VISIBLE);
                setaLazer.setRotation(90f); // Gira a seta para baixo
                } else {
                    conteudoLazer.setVisibility(View.GONE);
                    setaLazer.setRotation(0f); // Seta volta para a direita
                }
        });

        headerReferencia.setOnClickListener(v -> {
            if (conteudoReferencia.getVisibility() == View.GONE) {
                conteudoReferencia.setVisibility(View.VISIBLE);
                setaReferencia.setRotation(90f); // Gira a seta para baixo
                } else {
                    conteudoReferencia.setVisibility(View.GONE);
                    setaReferencia.setRotation(0f); // Seta volta para a direita
            }
        });

        headerAprendizado.setOnClickListener(v -> {
            if (conteudoAprendizado.getVisibility() == View.GONE) {
                conteudoAprendizado.setVisibility(View.VISIBLE);
                setaAprendizado.setRotation(90f); // Gira a seta para baixo
                } else {
                    conteudoAprendizado.setVisibility(View.GONE);
                    setaAprendizado.setRotation(0f); // Seta volta para a direita
            }
        });


        // ####  Botao voltar padrão  #### //
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            View content = findViewById(R.id.layout_conteudo_cinza);
            content.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });
    }

    // #####  BANCO DE DADOS  ##### //
    private void carregarCardsDoBanco() {
        // 1. Busca os cards da categoria Pessoal em segundo plano usando o seu 'db' global
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ItemCard> listaItens = db.itemCardDao().buscarPorCategoria(CategoriaItem.PESSOAL);

            // 2. Volta para a thread de interface para desenhar na tela
            runOnUiThread(() -> {
                GridLayout gridPessoal = findViewById(R.id.conteudo_pessoal);
                if (gridPessoal == null) return; // Evita erro se a tela ainda não carregou

                gridPessoal.removeAllViews(); // Evita duplicar os cards ao recarregar

                if (listaItens == null || listaItens.isEmpty()) return;

                for (ItemCard itemCardAtual : listaItens) {
                    final ItemCard card = itemCardAtual;

                    // Infla o layout do card respeitando as propriedades do nó pai
                    View cardView = getLayoutInflater().inflate(R.layout.item_card, gridPessoal, false);

                    // Referências dos componentes internos do card
                    androidx.cardview.widget.CardView cardRoot = cardView.findViewById(R.id.card_root);
                    ImageView imgEstrela = cardView.findViewById(R.id.img_estrela_favorito);
                    ImageView imgSimbolo = cardView.findViewById(R.id.img_card_simbolo);
                    TextView txtFala = cardView.findViewById(R.id.txt_card_fala);

                    txtFala.setText(card.getFala());

                    // BLINDAGEM: Lendo Ícones Nativos (Números) ou Fotos da Galeria (URI)
                    String uriOuIcone = card.getImagemUri();
                    if (uriOuIcone != null && !uriOuIcone.isEmpty()) {
                        try {
                            if (uriOuIcone.matches("\\d+")) {
                                imgSimbolo.setImageResource(Integer.parseInt(uriOuIcone));
                            } else {
                                imgSimbolo.setImageURI(Uri.parse(uriOuIcone));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                        }
                    } else {
                        imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                    }

                    if (card.isFavorito()) {
                        imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        imgEstrela.setImageResource(android.R.drawable.btn_star_big_off);
                    }

                    // CLIQUE SIMPLES: TEXT-TO-SPEECH (TTS)
                    cardRoot.setOnClickListener(v -> {
                        String textoParaFalar = txtFala.getText().toString().trim();
                        if (!textoParaFalar.isEmpty() && tts != null) {
                            tts.speak(textoParaFalar, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    });

                    // CLIQUE NA ESTRELA: FAVORITAR / DESFAVORITAR
                    imgEstrela.setOnClickListener(v -> {
                        boolean novoEstadoFavorito = !card.isFavorito();
                        card.setFavorito(novoEstadoFavorito);

                        if (novoEstadoFavorito) {
                            imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);
                        } else {
                            imgEstrela.setImageResource(android.R.drawable.btn_star_big_off);
                        }

                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.itemCardDao().atualizar(card);
                        });
                    });

                    // CLIQUE LONGO: MENU DE CONTEXTO (EDITAR / EXCLUIR)
                    cardRoot.setOnLongClickListener(v -> {
                        PopupMenu popup = new PopupMenu(MainActivity.this, cardRoot);
                        popup.getMenu().add(0, 1, 0, "Editar Imagem e Fala");
                        popup.getMenu().add(0, 2, 1, "Excluir Card");

                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case 1:
                                    abrirDialogEditarCard(card, txtFala, imgSimbolo);
                                    return true;
                                case 2:
                                    gridPessoal.removeView(cardView);
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        db.itemCardDao().deletar(card);
                                    });
                                    Toast.makeText(MainActivity.this, "Card excluído", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        });

                        popup.show();
                        return true;
                    });

                    // ========================================================
                    // CONFIGURAÇÃO DO GRID (Deixando o XML ditar a altura)
                    // ========================================================
                    GridLayout.LayoutParams params = (GridLayout.LayoutParams) cardView.getLayoutParams();
                    if (params == null) {
                        params = new GridLayout.LayoutParams();
                    }

                    // Apenas dizemos ao Java para dividir a tela em partes iguais (peso 1)
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                    // APAGAMOS O params.height PARA ELE RESPEITAR OS SEUS 130dp DO XML!
                    params.setMargins(12, 12, 12, 12);

                    cardView.setLayoutParams(params);

                    // Injeta o card estruturado no layout
                    gridPessoal.addView(cardView);
                }
            });
        });
    }
    private void configurarGavetaInternaCard(LinearLayout header, LinearLayout conteudo, TextView seta, String textoPadrao, String textoExpandido) {
        header.setOnClickListener(v -> {
            if (conteudo.getVisibility() == View.GONE) {
                conteudo.setVisibility(View.VISIBLE);
                seta.setText(textoExpandido); // Ex: "v sim"
            } else {
                conteudo.setVisibility(View.GONE);
                seta.setText(textoPadrao); // Ex: "> sim"
            }
        });
    }

    // #####  FALAR ##### //
    private void falar(String texto) {
        if (tts != null) {
            // QUEUE_FLUSH limpa a fila e fala agora
            // QUEUE_ADD terminaria de falar o atual para depois falar o novo
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    // O lançador que abre a galeria e devolve a imagem (URI)
    private final ActivityResultLauncher<String> abrirGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && imagemEmEdicaoAtual != null) {
                    // Se o usuário escolheu uma foto, joga ela no ImageView do Dialog
                    imagemEmEdicaoAtual.setImageURI(uri);
                }
            }
    );


    // #####  EDITAR CARD ##### //
    private void abrirDialogEditarCard(ItemCard itemCardAtual, TextView txtFalaOriginal, ImageView imgSimboloOriginal) {
        // 1. Infla o layout do diálogo customizado
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_card, null);

        // 2. Cria e configura o AlertDialog do Android
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // 3. DECLARAÇÃO LOCAL: Encontra as Views de dentro do XML do diálogo (dialog_edit_card.xml)
        FrameLayout containerImagem = dialogView.findViewById(R.id.container_editar_imagem);
        ImageView imgDialogPreview = dialogView.findViewById(R.id.img_dialog_preview);
        EditText editFala = dialogView.findViewById(R.id.edit_dialog_fala);
        TextView btnCancelar = dialogView.findViewById(R.id.btn_dialog_cancelar);

        // Vincula a variável global btnSalvarEdt que você declarou no topo da Activity
        btnSalvarEdt = dialogView.findViewById(R.id.btn_dialog_salvar);

        // 4. Preenche os campos do diálogo com os dados atuais do card
        editFala.setText(itemCardAtual.getFala());
        imgDialogPreview.setImageDrawable(imgSimboloOriginal.getDrawable());

        // 5. Configura a ação de clicar na imagem para abrir a galeria
        containerImagem.setOnClickListener(v -> {
            // Usa a sua variável global para rastrear qual ImageView vai receber a foto da galeria
            imagemEmEdicaoAtual = imgDialogPreview;
            abrirGaleria.launch("image/*");
        });

        // 6. Configura a ação do botão cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // 7. LÓGICA DO BOTÃO SALVAR (Corrigindo o print com as suas variáveis)
        btnSalvarEdt.setOnClickListener(vClick -> {
            String novoTexto = editFala.getText().toString().trim();

            // Verifica se imagemEmEdicaoAtual recebeu uma nova URI da galeria
            // Se a variável 'uriDaNovaImagem' não existir como global, você pode usar uma verificação de tag ou manter a atual
            String novaUri = itemCardAtual.getImagemUri();

            // Altera o visual do card correspondente diretamente na interface (na tela principal)
            txtFalaOriginal.setText(novoTexto);
            imgSimboloOriginal.setImageDrawable(imgDialogPreview.getDrawable());

            // Atualiza os dados do objeto do banco de dados (ItemCard)
            itemCardAtual.setFala(novoTexto);
            itemCardAtual.setImagemUri(novaUri);

            // --- SALVAR NO BANCO DE DADOS ---
            // Roda a atualização em segundo plano usando as threads do seu AppDatabase
            AppDatabase.databaseWriteExecutor.execute(() -> {
                // Inicializa a sua variável global 'db' se ela estiver nula, ou usa direto o getDatabase
                if (db == null) {
                    db = AppDatabase.getDatabase(MainActivity.this);
                }
                db.itemCardDao().atualizar(itemCardAtual);
            });

            Toast.makeText(MainActivity.this, "Salvo no banco de dados!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Mostra o popup na tela
        dialog.show();
    }

    private void verificarEPreencherBancoInicial() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Verifica se já existem cards na categoria PESSOAL
            List<ItemCard> lista = db.itemCardDao().buscarPorCategoria(CategoriaItem.PESSOAL);

            if (lista == null || lista.isEmpty()) {
                // O banco está vazio, vamos inserir os 5 cards padrão
                db.itemCardDao().inserir(new ItemCard("Eu", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_myplaces)));
                db.itemCardDao().inserir(new ItemCard("Você", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.button_onoff_indicator_on)));
                db.itemCardDao().inserir(new ItemCard("Ajuda", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_help)));
                db.itemCardDao().inserir(new ItemCard("Mais", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_add)));
                db.itemCardDao().inserir(new ItemCard("Meu", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_myplaces)));
            }

            // CORREÇÃO: Fora do IF! Agora os cards serão lidos sempre que o app abrir.
            carregarCardsDoBanco();
        });
    }
}