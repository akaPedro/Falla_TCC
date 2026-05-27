package com.example.falla.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
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
import com.example.falla.card.ItemHistorico;
import com.example.falla.historico.HistoricoActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private ImageView ImgFll;
    private View ImgPerf;
    private ImageView imgProfileFoto;
    private DrawerLayout drawerLayout;
    private AppCompatImageView imgMenu;
    // Itens da barra lateral
    private TextView itemTamanho, itemCores, itemHistorico, itemSobre, itemGrande, itemPequeno, btnSalvarEdt;
    private LinearLayout headerPessoal, headerFavoritos, headerComidas, headerLazer, headerAprendizado, submenuTamanho;
    private ImageView setaPessoal, setaFavoritos, setaComidas, setaLazer, setaAprendizado;
    private LinearLayout conteudoPessoal, conteudoComidas, conteudoLazer, conteudoAprendizado;

    // Cards coringas
    private GridLayout gridCoringaPessoal, gridCoringaAlimentos, gridCoringaLazer, gridCoringaAprendizado;
    // Subgavetas Pessoal
    private GridLayout gridSubPessoalEu, gridSubPessoalReferencia, gridSubPessoalSaude, gridSubPessoalCuidados, gridSubPessoalRoupas, gridSubPessoalAcoes;

    // Subgavetas Comidas
    private GridLayout gridSubComidasRefeicao, gridSubComidasCafe, gridSubComidasBebidas, gridSubComidasDoces;

    // Subgavetas Lazer
    private GridLayout gridSubLazerJogos, gridSubLazerTelas, gridSubLazerExterno, gridSubLazerSocial;

    // Subgavetas Aprendizado
    private GridLayout conteudoFavoritos, gridSubAprendNumeros, gridSubAprendAlfabeto, gridSubAprendVogais, gridSubAprendCores, gridSubAprendFormas;    // Variável temporária para guardar em qual ImageView vamos colocar a foto da galeria
    private ImageView imagemEmEdicaoAtual = null;

    private AppDatabase db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verifica se é o primeiro uso
        SharedPreferences prefs = getSharedPreferences("ConfigFalla", MODE_PRIVATE);
        if (!prefs.getBoolean("onboarding_concluido", false)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        // #####  BANCO DE DADOS  ##### //
        db = AppDatabase.getDatabase(MainActivity.this);
        // Carrega os cards do banco em segundo plano
        inserirCardsPadrao();
        carregarCardsFavoritos();


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
        imgProfileFoto = findViewById(R.id.img_profile_foto);


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

        // Cards coringas
        gridCoringaPessoal = findViewById(R.id.grid_coringa_pessoal);
        gridCoringaAlimentos = findViewById(R.id.grid_coringa_alimentos);
        gridCoringaLazer = findViewById(R.id.grid_coringa_lazer);
        gridCoringaAprendizado = findViewById(R.id.grid_coringa_aprendizado);


        // Subgavetas Pessoal
        gridSubPessoalEu            = findViewById(R.id.grid_sub_pessoal_eu);
        gridSubPessoalReferencia = findViewById(R.id.grid_sub_pessoal_referencia);
        gridSubPessoalSaude   = findViewById(R.id.grid_sub_pessoal_saude);
        gridSubPessoalCuidados      = findViewById(R.id.grid_sub_pessoal_cuidados);
        gridSubPessoalRoupas        = findViewById(R.id.grid_sub_pessoal_roupas);
        gridSubPessoalAcoes         = findViewById(R.id.grid_sub_pessoal_acoes);

        // Subgavetas Comidas
        gridSubComidasRefeicao  = findViewById(R.id.grid_sub_comidas_refeicao);
        gridSubComidasCafe      = findViewById(R.id.grid_sub_comidas_cafe);
        gridSubComidasBebidas   = findViewById(R.id.grid_sub_comidas_bebidas);
        gridSubComidasDoces     = findViewById(R.id.grid_sub_comidas_doces);

        // Subgavetas Lazer
        gridSubLazerJogos   = findViewById(R.id.grid_sub_lazer_jogos);
        gridSubLazerTelas   = findViewById(R.id.grid_sub_lazer_telas);
        gridSubLazerExterno = findViewById(R.id.grid_sub_lazer_externo);
        gridSubLazerSocial  = findViewById(R.id.grid_sub_lazer_social);

        // Subgavetas Aprendizado
        gridSubAprendNumeros  = findViewById(R.id.grid_sub_aprend_numeros);
        gridSubAprendAlfabeto = findViewById(R.id.grid_sub_aprend_alfabeto);
        gridSubAprendVogais   = findViewById(R.id.grid_sub_aprend_vogais);
        gridSubAprendCores    = findViewById(R.id.grid_sub_aprend_cores);
        gridSubAprendFormas   = findViewById(R.id.grid_sub_aprend_formas);





        // #####  HEADERS ##### //
        headerPessoal = findViewById(R.id.header_pessoal);
        headerFavoritos = findViewById(R.id.header_favorito);
        headerComidas = findViewById(R.id.header_Alimentos);
        headerLazer = findViewById(R.id.header_lazer);
        headerAprendizado = findViewById(R.id.header_aprendizado);

        // #####  SETAS ##### //
        setaPessoal = findViewById(R.id.seta_pessoal);
        setaFavoritos = findViewById(R.id.seta_favorito);
        setaComidas = findViewById(R.id.seta_alimento);
        setaLazer = findViewById(R.id.seta_lazer);
        setaAprendizado = findViewById(R.id.seta_aprendizado);

        // #####  CONTEUDO / GRIDS  ##### //
        conteudoFavoritos = findViewById(R.id.conteudo_favoritos);
        conteudoPessoal = findViewById(R.id.conteudo_pessoal);
        conteudoComidas = findViewById(R.id.conteudo_alimentos);
        conteudoLazer = findViewById(R.id.conteudo_lazer);
        conteudoAprendizado = findViewById(R.id.conteudo_aprendizado);

        // #####  LOGICA DE TAMANHO  ##### //
        itemGrande.setOnClickListener(v -> {
            salvarEAplicarColunas(2); // Salva 2 colunas e aplica
            drawerLayout.closeDrawers(); // Opcional: fecha a barra lateral ao clicar
        });

        itemPequeno.setOnClickListener(v -> {
            salvarEAplicarColunas(3); // Salva 3 colunas e aplica
            drawerLayout.closeDrawers(); // Opcional: fecha a barra lateral ao clicar
        });


        conteudoAprendizado = findViewById(R.id.conteudo_aprendizado);

        // tamanho salvo anteriormente, 2 colunas como padrão.
        android.content.SharedPreferences pref = getSharedPreferences("ConfigFalla", MODE_PRIVATE);
        int colunasSalvas = pref.getInt("quantidade_colunas", 2);
        salvarEAplicarColunas(colunasSalvas);


        // #### Barra latreral #### //
        itemTamanho.setOnClickListener(v -> {
            if (submenuTamanho.getVisibility() == View.GONE) {
                submenuTamanho.setVisibility(View.VISIBLE);
            } else {
                submenuTamanho.setVisibility(View.GONE);
            }
        });

        itemCores.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            abrirDialogCores();
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

        itemHistorico.setOnClickListener(v -> {
            Intent historicointent = new Intent(MainActivity.this, HistoricoActivity.class);
            startActivity(historicointent);
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
        findViewById(R.id.item_mais).setOnClickListener(v -> falar("Mais"));


        // Falas do Card Não
        findViewById(R.id.item_nao_quero).setOnClickListener(v -> falar("Não, eu não quero"));
        findViewById(R.id.item_nao_gosto).setOnClickListener(v -> falar("Não, eu não gosto"));
        findViewById(R.id.item_nao_ruim).setOnClickListener(v -> falar("Isso é ruim"));
        findViewById(R.id.item_menos).setOnClickListener(v -> falar("Menos"));



        // Conectar cliques das subgavetas
        configurarSubgaveta(R.id.header_sub_pessoal_eu,          R.id.seta_sub_pessoal_eu,          gridSubPessoalEu);
        configurarSubgaveta(R.id.header_sub_pessoal_referencia,  R.id.seta_sub_pessoal_referencia,  gridSubPessoalReferencia);
            configurarSubgaveta(R.id.header_sub_pessoal_saude,   R.id.seta_sub_pessoal_saude,       gridSubPessoalSaude);
        configurarSubgaveta(R.id.header_sub_pessoal_cuidados,    R.id.seta_sub_pessoal_cuidados,    gridSubPessoalCuidados);
        configurarSubgaveta(R.id.header_sub_pessoal_roupas,      R.id.seta_sub_pessoal_roupas,      gridSubPessoalRoupas);
        configurarSubgaveta(R.id.header_sub_pessoal_acoes,       R.id.seta_sub_pessoal_acoes,       gridSubPessoalAcoes);

        configurarSubgaveta(R.id.header_sub_comidas_refeicao, R.id.seta_sub_comidas_refeicao, gridSubComidasRefeicao);
        configurarSubgaveta(R.id.header_sub_comidas_cafe,     R.id.seta_sub_comidas_cafe,     gridSubComidasCafe);
        configurarSubgaveta(R.id.header_sub_comidas_bebidas,  R.id.seta_sub_comidas_bebidas,  gridSubComidasBebidas);
        configurarSubgaveta(R.id.header_sub_comidas_doces,    R.id.seta_sub_comidas_doces,    gridSubComidasDoces);

        configurarSubgaveta(R.id.header_sub_lazer_jogos,   R.id.seta_sub_lazer_jogos,   gridSubLazerJogos);
        configurarSubgaveta(R.id.header_sub_lazer_telas,   R.id.seta_sub_lazer_telas,   gridSubLazerTelas);
        configurarSubgaveta(R.id.header_sub_lazer_externo, R.id.seta_sub_lazer_externo, gridSubLazerExterno);
        configurarSubgaveta(R.id.header_sub_lazer_social,  R.id.seta_sub_lazer_social,  gridSubLazerSocial);

        configurarSubgaveta(R.id.header_sub_aprend_numeros,  R.id.seta_sub_aprend_numeros,  gridSubAprendNumeros);
        configurarSubgaveta(R.id.header_sub_aprend_alfabeto, R.id.seta_sub_aprend_alfabeto, gridSubAprendAlfabeto);
        configurarSubgaveta(R.id.header_sub_aprend_vogais,   R.id.seta_sub_aprend_vogais,   gridSubAprendVogais);
        configurarSubgaveta(R.id.header_sub_aprend_cores,    R.id.seta_sub_aprend_cores,    gridSubAprendCores);
        configurarSubgaveta(R.id.header_sub_aprend_formas,   R.id.seta_sub_aprend_formas,   gridSubAprendFormas);




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
    private void carregarGridEspecifico(CategoriaItem categoria, GridLayout gridGaveta) {
        if (gridGaveta == null) return; // Evita quebrar se a gaveta não existir na tela

        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Busca apenas os cards da categoria que foi solicitada
            List<ItemCard> listaItens = db.itemCardDao().buscarPorCategoria(categoria);

            runOnUiThread(() -> {
                gridGaveta.removeAllViews(); // Limpa os cards antigos apenas desta gaveta

                if (listaItens == null || listaItens.isEmpty()) return;

                for (ItemCard itemCardAtual : listaItens) {
                    final ItemCard card = itemCardAtual;

                    // Infla o card DENTRO da gaveta correta
                    View cardView = getLayoutInflater().inflate(R.layout.item_card, gridGaveta, false);

                    androidx.cardview.widget.CardView cardRoot = cardView.findViewById(R.id.card_root);
                    ImageView imgEstrela = cardView.findViewById(R.id.img_estrela_favorito);
                    ImageView imgSimbolo = cardView.findViewById(R.id.img_card_simbolo);
                    TextView txtFala = cardView.findViewById(R.id.txt_card_fala);

                    txtFala.setText(card.getTexto());

                    // Tratamento da Imagem (URI da Galeria ou Ícone do Android)
                    String uriOuIcone = card.getImagemUri();
                    if (uriOuIcone != null && !uriOuIcone.isEmpty()) {
                        try {
                            if (uriOuIcone.matches("\\d+")) {
                                imgSimbolo.setImageResource(Integer.parseInt(uriOuIcone));
                            } else {
                                imgSimbolo.setImageURI(android.net.Uri.parse(uriOuIcone));
                            }
                        } catch (Exception e) {
                            imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                        }
                    } else {
                        imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                    }

                    // Verifica se tem estrela
                    if (card.isFavorito()) imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);
                    else imgEstrela.setImageResource(android.R.drawable.btn_star_big_off);

                    // TTS (Falar)
                    cardRoot.setOnClickListener(v -> {
                        String textoParaFalar = txtFala.getText().toString().trim();
                        if (!textoParaFalar.isEmpty() && tts != null) {
                            tts.speak(textoParaFalar, TextToSpeech.QUEUE_ADD, null, null);

                            // Registra no histórico
                            registrarNoHistorico(card.getImagemUri(), card.getFala());
                        }
                    });

                    // ✅ Aplica a cor da categoria no card
                    cardRoot.setCardBackgroundColor(obterCorCategoria(categoria));


                    // Favoritar / Desfavoritar
                    imgEstrela.setOnClickListener(v -> {
                        boolean novoEstadoFavorito = !card.isFavorito();
                        card.setFavorito(novoEstadoFavorito);

                        if (novoEstadoFavorito) imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);
                        else imgEstrela.setImageResource(android.R.drawable.btn_star_big_off);

                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.itemCardDao().atualizar(card);
                            // Atualiza a gaveta de favoritos instantaneamente
                            carregarCardsFavoritos();
                        });
                    });

                    // Menu Long Click (Excluir)
                    cardRoot.setOnLongClickListener(v -> {
                        android.widget.PopupMenu popup = new android.widget.PopupMenu(MainActivity.this, cardRoot);
                        popup.getMenu().add(0, 1, 0, "Editar Imagem e Fala");
                        popup.getMenu().add(0, 2, 1, "Excluir Card");

                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case 1:
                                    abrirDialogEditarCard(card, txtFala, imgSimbolo);
                                    return true;
                                case 2:
                                    gridGaveta.removeView(cardView); // Remove o card da gaveta específica
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        db.itemCardDao().deletar(card);
                                        carregarCardsFavoritos(); // Atualiza favoritos caso o card estivesse lá
                                    });
                                    android.widget.Toast.makeText(MainActivity.this, "Card excluído", android.widget.Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        });
                        popup.show();
                        return true;
                    });

                    // Ajuste do Card no Grid
                    android.widget.GridLayout.LayoutParams params = (android.widget.GridLayout.LayoutParams) cardView.getLayoutParams();
                    if (params == null) params = new android.widget.GridLayout.LayoutParams();
                    params.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
                    params.setMargins(12, 12, 12, 12);
                    cardView.setLayoutParams(params);

                    gridGaveta.addView(cardView);
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

    private void configurarSubgaveta(int headerId, int setaId, GridLayout grid) {
        LinearLayout header = findViewById(headerId);
        ImageView seta = findViewById(setaId);
        if (header == null || grid == null) return;

        header.setOnClickListener(v -> {
            if (grid.getVisibility() == View.GONE) {
                grid.setVisibility(View.VISIBLE);
                seta.setRotation(180f);
            } else {
                grid.setVisibility(View.GONE);
                seta.setRotation(0f);
            }
        });
    }


    // #####  FALAR ##### //
    private void falar(String texto) {
        if (tts != null) {
            // QUEUE_FLUSH limpa a fila e fala agora
            // QUEUE_ADD terminaria de falar o atual para depois falar o novo
            tts.speak(texto, TextToSpeech.QUEUE_ADD, null, null);
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

                    // A MÁGICA AQUI: Pede ao Android permissão PERMANENTE para ler essa imagem
                    // Sem isso, a URI "morre" assim que o aplicativo é fechado!
                    getContentResolver().takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // 1. Joga o visual da foto no ImageView do Dialog
                    imagemEmEdicaoAtual.setImageURI(uri);

                    // 2. GUARDA o caminho do texto dentro da Tag da View para usarmos ao salvar
                    imagemEmEdicaoAtual.setTag(uri.toString());
                }
            }
    );


    // #####  EDITAR CARD ##### //
    private void abrirDialogEditarCard(ItemCard itemCardAtual, TextView txtFalaOriginal, ImageView imgSimboloOriginal) {
        // 1. Infla o layout do diálogo customizado [cite: 164]
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_card, null);

        // 2. Cria e configura o AlertDialog do Android [cite: 165]
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView); //[cite: 166]
        android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // 3. DECLARAÇÃO LOCAL: Encontra as Views de dentro do XML do diálogo
        FrameLayout containerImagem = dialogView.findViewById(R.id.container_editar_imagem);
        ImageView imgDialogPreview = dialogView.findViewById(R.id.img_dialog_preview);
        EditText editFala = dialogView.findViewById(R.id.edit_dialog_fala);
        TextView btnCancelar = dialogView.findViewById(R.id.btn_dialog_cancelar);

        // Vincula a variável global btnSalvarEdt que você declarou no topo da Activity
        btnSalvarEdt = dialogView.findViewById(R.id.btn_dialog_salvar);

        // 4. Preenche os campos do diálogo com os dados atuais do card
        editFala.setText(itemCardAtual.getFala());
        imgDialogPreview.setImageDrawable(imgSimboloOriginal.getDrawable());

        // Limpa qualquer Tag antiga que tenha ficado na View para começar do zero
        imgDialogPreview.setTag(null);

        // 5. Configura a ação de clicar na imagem para abrir a galeria
        containerImagem.setOnClickListener(v -> {
            // Usa a sua variável global para rastrear qual ImageView vai receber a foto da galeria
            imagemEmEdicaoAtual = imgDialogPreview;
            abrirGaleria.launch("image/*");
        });

        // 6. Configura a ação do botão cancelar        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // 7. LÓGICA DO BOTÃO SALVAR CORRIGIDA
// 7. LÓGICA DO BOTÃO SALVAR CORRIGIDA
        btnSalvarEdt.setOnClickListener(vClick -> {
            String novoTexto = editFala.getText().toString().trim();

            // Verifica se imgDialogPreview recebeu uma nova URI da galeria através da Tag
            String novaUri = itemCardAtual.getImagemUri();
            if (imgDialogPreview.getTag() != null) {
                novaUri = imgDialogPreview.getTag().toString();
            }

            // Atualiza os dados do objeto que será enviado ao Room
            itemCardAtual.setFala(novoTexto);
            itemCardAtual.setImagemUri(novaUri);

            // --- SALVAR NO BANCO DE DADOS ---
            // Roda a atualização em segundo plano
            AppDatabase.databaseWriteExecutor.execute(() -> {
                if (db == null) {
                    db = AppDatabase.getDatabase(MainActivity.this);
                }

                // 1. Atualiza permanentemente no Room
                db.itemCardDao().atualizar(itemCardAtual);

                // 2. O SEGREDO: Assim que o banco terminar de salvar, roda este bloco na UI Thread
                runOnUiThread(() -> {
                    // Força todas as gavetas e favoritos a redesenharem-se com os dados novos
                    carregarTodasAsGavetas();
                    Toast.makeText(MainActivity.this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                });
            });

            // Fecha o diálogo de edição
            dialog.dismiss();
        });
        // Mostra o popup na tela
        dialog.show();
    }
    private void inserirCardsPadrao() {
        AppDatabase.databaseWriteExecutor.execute(() -> {

            // Checa TODAS as categorias coringa, não só PESSOAL
            List<ItemCard> pessoal     = db.itemCardDao().buscarPorCategoria(CategoriaItem.PESSOAL);
            List<ItemCard> comidas     = db.itemCardDao().buscarPorCategoria(CategoriaItem.COMIDAS);
            List<ItemCard> lazer       = db.itemCardDao().buscarPorCategoria(CategoriaItem.LAZER);
            List<ItemCard> aprendizado = db.itemCardDao().buscarPorCategoria(CategoriaItem.APRENDIZADO);

            boolean pessoalOk     = pessoal     != null && !pessoal.isEmpty();
            boolean comidasOk     = comidas     != null && !comidas.isEmpty();
            boolean lazerOk       = lazer       != null && !lazer.isEmpty();
            boolean aprendizadoOk = aprendizado != null && !aprendizado.isEmpty();

            // Se todos já existem, não faz nada
            if (pessoalOk && comidasOk && lazerOk && aprendizadoOk) return;

            String nomeUsuario = "...";
            com.example.falla.usuario.Usuario user = db.usuarioDao().getUsuario();
            if (user != null && user.nome != null && !user.nome.isEmpty()) {
                nomeUsuario = user.nome;
            }
            final String nomeFinal = nomeUsuario;

            java.util.List<ItemCard> cards = new java.util.ArrayList<>();
            String ico = String.valueOf(android.R.drawable.ic_menu_add);

            // Insere apenas os grupos que estão faltando
            if (!pessoalOk) {
                cards.add(new ItemCard("Quero ir ao banheiro",    CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_directions)));
                cards.add(new ItemCard("Estou com dor",           CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_help)));
                cards.add(new ItemCard("Meu nome é " + nomeFinal, CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_myplaces)));

                for (String s : new String[]{"Estou feliz","Estou triste","Estou com raiva",
                        "Estou cansado","Estou com medo","Quero ficar sozinho","Quero um abraço"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_EU, ico));

                for (String s : new String[]{"Eu","Você","Ele","Ela","A gente","Nós",
                        "Isto","Isso","Aquilo","Aquele","Aqui","Lá","Ali","Meu","Minha"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_REFERENCIA, ico));

                for (String s : new String[]{"Dor de cabeça","Dor de barriga","Estou enjoado",
                        "Estou com febre","Preciso de remédio","Me machuquei"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_SAUDE, ico));

                for (String s : new String[]{"Tomar banho","Escovar os dentes","Lavar as mãos",
                        "Pentear o cabelo","Assoar o nariz","Cortar a unha","Cortar o cabelo"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_CUIDADOS, ico));

                for (String s : new String[]{"Estou com frio","Estou com calor","Trocar de roupa",
                        "Vestir casaco","Calçar sapato","Tirar o sapato","Colocar pijama"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_ROUPAS, ico));

                for (String s : new String[]{"Ir","Ver","Parar","Esperar"})
                    cards.add(new ItemCard(s, CategoriaItem.PESSOAL_ACOES, ico));
            }

            if (!comidasOk) {
                cards.add(new ItemCard("Estou com fome", CategoriaItem.COMIDAS, String.valueOf(android.R.drawable.ic_menu_help)));
                cards.add(new ItemCard("Estou com sede", CategoriaItem.COMIDAS, String.valueOf(android.R.drawable.ic_menu_help)));

                for (String s : new String[]{"Arroz e feijão","Carne","Frango","Macarrão",
                        "Peixe","Sopa","Salada","Picadinho","Purê de batata","Pizza","Hambúrguer"})
                    cards.add(new ItemCard(s, CategoriaItem.COMIDAS_REFEICAO, ico));

                for (String s : new String[]{"Pão","Bolo","Biscoito","Fruta",
                        "Danone","Queijo","Presunto","Cereal"})
                    cards.add(new ItemCard(s, CategoriaItem.COMIDAS_CAFE_LANCHES, ico));

                for (String s : new String[]{"Água","Suco","Café","Leite",
                        "Achocolatado","Refrigerante","Chá"})
                    cards.add(new ItemCard(s, CategoriaItem.COMIDAS_BEBIDAS, ico));

                for (String s : new String[]{"Chocolate","Sorvete","Gelatina",
                        "Pudim","Doce","Brigadeiro"})
                    cards.add(new ItemCard(s, CategoriaItem.COMIDAS_DOCES, ico));
            }

            if (!lazerOk) {
                cards.add(new ItemCard("Quero brincar", CategoriaItem.LAZER, String.valueOf(android.R.drawable.ic_menu_help)));
                cards.add(new ItemCard("Quero passear", CategoriaItem.LAZER, String.valueOf(android.R.drawable.ic_menu_help)));

                for (String s : new String[]{"Videogame","Jogo de tabuleiro","Cartas",
                        "Quebra-cabeça","Blocos de montar","Meus brinquedos"})
                    cards.add(new ItemCard(s, CategoriaItem.LAZER_JOGOS, ico));

                for (String s : new String[]{"Assistir TV","Assistir YouTube","Ver filme",
                        "Ver desenho","Ouvir música","Celular","Tablet"})
                    cards.add(new ItemCard(s, CategoriaItem.LAZER_TELAS, ico));

                for (String s : new String[]{"Ir ao parque","Ir à praça","Jogar futebol",
                        "Jogar vôlei","Ir à piscina","Ir à praia",
                        "Passear de carro","Passear de moto","Dar uma caminhada"})
                    cards.add(new ItemCard(s, CategoriaItem.LAZER_EXTERNO, ico));

                for (String s : new String[]{"Brincar com amigos","Conversar","Visitar família",
                        "Brincar com cachorro","Brincar com gato","Fazer uma ligação"})
                    cards.add(new ItemCard(s, CategoriaItem.LAZER_SOCIAL, ico));
            }

            if (!aprendizadoOk) {
                cards.add(new ItemCard("Quero estudar",    CategoriaItem.APRENDIZADO, String.valueOf(android.R.drawable.ic_menu_help)));
                cards.add(new ItemCard("Preciso de ajuda", CategoriaItem.APRENDIZADO, String.valueOf(android.R.drawable.ic_menu_help)));

                for (int i = 1; i <= 100; i++)
                    cards.add(new ItemCard(String.valueOf(i), CategoriaItem.APRENDIZADO_NUMEROS, ico));

                for (char c = 'A'; c <= 'Z'; c++)
                    cards.add(new ItemCard(String.valueOf(c), CategoriaItem.APRENDIZADO_ALFABETO, ico));

                for (char v : new char[]{'A','E','I','O','U'})
                    cards.add(new ItemCard(String.valueOf(v), CategoriaItem.APRENDIZADO_VOGAIS, ico));

                for (String s : new String[]{"Vermelho","Azul","Amarelo","Verde","Laranja",
                        "Roxo","Rosa","Marrom","Preto","Branco","Cinza"})
                    cards.add(new ItemCard(s, CategoriaItem.APRENDIZADO_CORES, ico));

                for (String s : new String[]{"Círculo","Quadrado","Triângulo","Retângulo",
                        "Oval","Estrela","Coração","Losango","Hexágono"})
                    cards.add(new ItemCard(s, CategoriaItem.APRENDIZADO_FORMAS, ico));
            }

            for (ItemCard card : cards)
                db.itemCardDao().inserir(card);

            runOnUiThread(() -> carregarTodasAsGavetas());
        });
    }

    // ####### FAVORITOS ####### //
    private void carregarCardsFavoritos() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Busca apenas os cards com isFavorito = 1
            List<ItemCard> listaFavoritos = db.itemCardDao().buscarFavoritos();

            // 2. Desenha na interface da gaveta
            runOnUiThread(() -> {
                // SUBSTITUA PELO ID REAL DO SEU LAYOUT DE FAVORITOS NA GAVETA
                GridLayout gridFavoritos = findViewById(R.id.conteudo_favoritos);
                if (gridFavoritos == null) return;

                gridFavoritos.removeAllViews(); // Limpa antes de redesenhar

                if (listaFavoritos == null || listaFavoritos.isEmpty()) return;

                for (ItemCard card : listaFavoritos) {
                    // Reaproveitamos o mesmo visual do card
                    View cardView = getLayoutInflater().inflate(R.layout.item_card, gridFavoritos, false);

                    androidx.cardview.widget.CardView cardRoot = cardView.findViewById(R.id.card_root);
                    ImageView imgEstrela = cardView.findViewById(R.id.img_estrela_favorito);
                    ImageView imgSimbolo = cardView.findViewById(R.id.img_card_simbolo);
                    TextView txtFala = cardView.findViewById(R.id.txt_card_fala);

                    txtFala.setText(card.getFala());

                    // Na gaveta, a estrela sempre aparece ligada
                    imgEstrela.setImageResource(android.R.drawable.btn_star_big_on);

                    // Lógica de leitura de imagem (Idêntica à principal)
                    String uriOuIcone = card.getImagemUri();
                    if (uriOuIcone != null && !uriOuIcone.isEmpty()) {
                        try {
                            if (uriOuIcone.matches("\\d+")) imgSimbolo.setImageResource(Integer.parseInt(uriOuIcone));
                            else imgSimbolo.setImageURI(Uri.parse(uriOuIcone));
                        } catch (Exception e) {
                            imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                        }
                    } else {
                        imgSimbolo.setImageResource(android.R.drawable.ic_menu_help);
                    }

                    // TTS ao clicar no card na gaveta
                    cardRoot.setOnClickListener(v -> {
                        String textoParaFalar = txtFala.getText().toString().trim();
                        if (!textoParaFalar.isEmpty() && tts != null) {
                            tts.speak(textoParaFalar, TextToSpeech.QUEUE_ADD, null, null);
                        }
                    });

                    // Lógica de DESFAVORITAR direto da gaveta
                    imgEstrela.setOnClickListener(v -> {
                        card.setFavorito(false);
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.itemCardDao().atualizar(card);
                            // Atualiza AMBAS as telas para manter o espelho perfeito
                            carregarCardsFavoritos();
                            carregarTodasAsGavetas();
                        });
                    });

                    // Configuração do Layout do Card
                    GridLayout.LayoutParams params = (GridLayout.LayoutParams) cardView.getLayoutParams();
                    if (params == null) params = new GridLayout.LayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    params.setMargins(12, 12, 12, 12);
                    cardView.setLayoutParams(params);

                    gridFavoritos.addView(cardView);
                }
            });
        });
    }

    // ========================================================
    // ATUALIZA A TELA SEMPRE QUE VOLTAR PARA A MAIN
    // ========================================================
    @Override
    protected void onResume() {
        super.onResume();

        if (db == null) {
            db = AppDatabase.getDatabase(MainActivity.this);
        }

        carregarTodasAsGavetas();

        // carrega a foto do perfil na toolbar
        AppDatabase.databaseWriteExecutor.execute(() -> {
            com.example.falla.usuario.Usuario user = db.usuarioDao().getUsuario();
            runOnUiThread(() -> {
                if (user != null && user.caminhoFoto != null) {
                    imgProfileFoto.setImageTintList(null); // remove o tint verde
                    imgProfileFoto.setImageURI(android.net.Uri.fromFile(new java.io.File(user.caminhoFoto)));
                } else {
                    // sem foto: mantém o ícone padrão com tint
                    imgProfileFoto.setImageResource(android.R.drawable.ic_menu_myplaces);
                    imgProfileFoto.setImageTintList(
                            android.content.res.ColorStateList.valueOf(
                                    androidx.core.content.ContextCompat.getColor(this, R.color.texto_header)
                            )
                    );
                }
            });
        });
    }

    private void carregarTodasAsGavetas() {
        carregarCardsFavoritos();

        // Coringas
        carregarGridEspecifico(CategoriaItem.PESSOAL, gridCoringaPessoal);
        carregarGridEspecifico(CategoriaItem.COMIDAS, gridCoringaAlimentos);
        carregarGridEspecifico(CategoriaItem.LAZER, gridCoringaLazer);
        carregarGridEspecifico(CategoriaItem.APRENDIZADO, gridCoringaAprendizado);

        // Subgavetas Pessoal
        carregarGridEspecifico(CategoriaItem.PESSOAL_EU,           gridSubPessoalEu);
        carregarGridEspecifico(CategoriaItem.PESSOAL_REFERENCIA,   gridSubPessoalReferencia);
        carregarGridEspecifico(CategoriaItem.PESSOAL_SAUDE,        gridSubPessoalSaude);
        carregarGridEspecifico(CategoriaItem.PESSOAL_CUIDADOS,     gridSubPessoalCuidados);
        carregarGridEspecifico(CategoriaItem.PESSOAL_ROUPAS,       gridSubPessoalRoupas);
        carregarGridEspecifico(CategoriaItem.PESSOAL_ACOES,        gridSubPessoalAcoes);

        // Subgavetas Alimentos
        carregarGridEspecifico(CategoriaItem.COMIDAS_REFEICAO,    gridSubComidasRefeicao);
        carregarGridEspecifico(CategoriaItem.COMIDAS_CAFE_LANCHES, gridSubComidasCafe);
        carregarGridEspecifico(CategoriaItem.COMIDAS_BEBIDAS,     gridSubComidasBebidas);
        carregarGridEspecifico(CategoriaItem.COMIDAS_DOCES,       gridSubComidasDoces);

        // Subgavetas Lazer
        carregarGridEspecifico(CategoriaItem.LAZER_JOGOS,    gridSubLazerJogos);
        carregarGridEspecifico(CategoriaItem.LAZER_TELAS,    gridSubLazerTelas);
        carregarGridEspecifico(CategoriaItem.LAZER_EXTERNO,  gridSubLazerExterno);
        carregarGridEspecifico(CategoriaItem.LAZER_SOCIAL,   gridSubLazerSocial);

        // Subgavetas Aprendizado
        carregarGridEspecifico(CategoriaItem.APRENDIZADO_NUMEROS,  gridSubAprendNumeros);
        carregarGridEspecifico(CategoriaItem.APRENDIZADO_ALFABETO, gridSubAprendAlfabeto);
        carregarGridEspecifico(CategoriaItem.APRENDIZADO_VOGAIS,   gridSubAprendVogais);
        carregarGridEspecifico(CategoriaItem.APRENDIZADO_CORES,    gridSubAprendCores);
        carregarGridEspecifico(CategoriaItem.APRENDIZADO_FORMAS,   gridSubAprendFormas);
    }
    // ========================================================
    // SALVA E APLICA O TAMANHO DOS CARDS PERMANENTEMENTE
    // ========================================================
    private void salvarEAplicarColunas(int quantidadeColunas) {
        // 1. Salva a escolha
        getSharedPreferences("ConfigFalla", MODE_PRIVATE).edit().putInt("quantidade_colunas", quantidadeColunas).apply();

        // 2. Lista de TODOS os GridLayouts reais do app
        GridLayout[] todosOsGrids = {
                // Favoritos
                conteudoFavoritos,

                // Cards coringas
                gridCoringaPessoal, gridCoringaAlimentos,
                gridCoringaLazer, gridCoringaAprendizado,


                // Subgavetas Pessoal
                gridSubPessoalEu, gridSubPessoalSaude, gridSubPessoalCuidados,
                gridSubPessoalRoupas, gridSubPessoalAcoes, gridSubPessoalReferencia,

                // Subgavetas Comidas
                gridSubComidasRefeicao, gridSubComidasCafe,
                gridSubComidasBebidas, gridSubComidasDoces,

                // Subgavetas Lazer
                gridSubLazerJogos, gridSubLazerTelas,
                gridSubLazerExterno, gridSubLazerSocial,

                // Subgavetas Aprendizado
                gridSubAprendNumeros, gridSubAprendAlfabeto, gridSubAprendVogais,
                gridSubAprendCores, gridSubAprendFormas
        };

        // 3. Limpa e redefine colunas em todos os grids
        for (GridLayout g : todosOsGrids) {
            if (g != null) {
                g.removeAllViews();
                g.setColumnCount(quantidadeColunas);
            }
        }

        // 4. Recarrega os cards no novo formato
        if (db != null) {
            carregarTodasAsGavetas();
        }
    }


    // ###### HISTORICO ###### //
    private void registrarNoHistorico(String uriOuIcone, String fala) {
        java.text.SimpleDateFormat formataData = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        java.text.SimpleDateFormat formataHora = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        java.util.Date agora = new java.util.Date();

        ItemHistorico novoRegistro = new ItemHistorico(
                formataData.format(agora),
                formataHora.format(agora),
                uriOuIcone,
                fala  // <- passa o texto
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (db != null) db.historicoDao().inserir(novoRegistro);
        });
    }

    private void abrirDialogCores() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cores, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinner_categoria_cor);
        android.widget.GridLayout gridPaleta = dialogView.findViewById(R.id.grid_paleta_cores);
        androidx.cardview.widget.CardView cardPreview = dialogView.findViewById(R.id.card_preview_cor);
        TextView btnCancelar = dialogView.findViewById(R.id.btn_cores_cancelar);
        TextView btnSalvar = dialogView.findViewById(R.id.btn_cores_salvar);

        // Categorias disponíveis (sem FAVORITOS)
        CategoriaItem[] categorias = {
                CategoriaItem.PESSOAL,
                CategoriaItem.COMIDAS,
                CategoriaItem.LAZER,
                CategoriaItem.APRENDIZADO
        };

        android.widget.ArrayAdapter<CategoriaItem> adapterCat =
                new android.widget.ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapterCat);

        // Paleta de cores suaves para autistas
        // Formato: {cor em int, nome para acessibilidade}
        int[] paleta = {
                // Neutros
                0xFFFFFFFF, // Branco
                0xFFF4F4F6, // Cinza claro
                0xFFDDE3E0, // Cinza esverdeado
                0xFFD6D3CC, // Bege acinzentado
                0xFFBFC5C2, // Cinza médio

                // Verdes suaves
                0xFFA8D5B5, // Verde menta
                0xFFB8D8C8, // Verde sage
                0xFFC8E6D4, // Verde água
                0xFF9ECFB0, // Verde folha suave
                0xFF7DB89A, // Verde musgo claro

                // Azuis suaves
                0xFFC2DCE8, // Azul bebê
                0xFFB5D0E0, // Azul claro
                0xFFA8C8DC, // Azul sereno
                0xFFD0E8F2, // Azul gelo
                0xFF8EC0D8, // Azul piscina suave

                // Lilás/Roxo suave
                0xFFD4C5E2, // Lilás claro
                0xFFC8B8D8, // Lilás médio
                0xFFBCADD0, // Lavanda
                0xFFE2D8EE, // Lilás quase branco
                0xFFAA9DC4, // Roxo dessaturado

                // Terrosos/Quentes suaves
                0xFFF5D9C2, // Pêssego
                0xFFEDCFB8, // Laranja claro
                0xFFF2E2D0, // Creme
                0xFFE8D0BC, // Bege quente
                0xFFD4B8A0, // Caramelo claro

                // Rosa suave
                0xFFF2C8CC, // Rosa bebê
                0xFFEDB8BC, // Rosé
                0xFFF5D8DA, // Rosa quase branco
                0xFFE8A8AD, // Rosa antigo
                0xFFDDA0A4, // Rosa empoeirado
        };

        // Cor selecionada atualmente (começa com a cor já salva da categoria)
        final int[] corSelecionada = {obterCorCategoria(categorias[0])};
        cardPreview.setCardBackgroundColor(corSelecionada[0]);

        // Atualiza a cor do preview quando muda a categoria
        spinnerCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                corSelecionada[0] = obterCorCategoria(categorias[pos]);
                cardPreview.setCardBackgroundColor(corSelecionada[0]);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Monta a paleta visualmente
        int tamanhoCirculo = (int) (48 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int cor : paleta) {
            final int corAtual = cor;

            android.widget.FrameLayout circuloContainer = new android.widget.FrameLayout(this);
            android.widget.GridLayout.LayoutParams paramsGrid = new android.widget.GridLayout.LayoutParams();
            paramsGrid.width = tamanhoCirculo;
            paramsGrid.height = tamanhoCirculo;
            paramsGrid.setMargins(margin, margin, margin, margin);
            circuloContainer.setLayoutParams(paramsGrid);

            // Círculo colorido
            android.view.View circulo = new android.view.View(this);
            android.widget.FrameLayout.LayoutParams paramsCir =
                    new android.widget.FrameLayout.LayoutParams(tamanhoCirculo, tamanhoCirculo);
            circulo.setLayoutParams(paramsCir);

            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            shape.setColor(corAtual);
            shape.setStroke(2, 0xFFCCCCCC);
            circulo.setBackground(shape);

            circulo.setOnClickListener(v -> {
                corSelecionada[0] = corAtual;
                cardPreview.setCardBackgroundColor(corAtual);
            });

            circuloContainer.addView(circulo);
            gridPaleta.addView(circuloContainer);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            CategoriaItem catEscolhida = categorias[spinnerCategoria.getSelectedItemPosition()];
            salvarCorCategoria(catEscolhida, corSelecionada[0]);
            carregarTodasAsGavetas(); // Redesenha os cards com a nova cor
            dialog.dismiss();
            drawerLayout.closeDrawers();
        });

        dialog.show();
    }

    // Lê a cor salva para uma categoria (retorna branco como padrão)
    private int obterCorCategoria(CategoriaItem categoria) {
        android.content.SharedPreferences prefs = getSharedPreferences("CoresCategoria", MODE_PRIVATE);
        return prefs.getInt("cor_" + categoria.name(), 0xFFFFFFFF);
    }

    // Salva a cor escolhida para a categoria
    private void salvarCorCategoria(CategoriaItem categoria, int cor) {
        android.content.SharedPreferences prefs = getSharedPreferences("CoresCategoria", MODE_PRIVATE);
        prefs.edit().putInt("cor_" + categoria.name(), cor).apply();
    }

}