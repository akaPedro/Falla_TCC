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
            salvarEAplicarColunas(2); // Salva 2 colunas e aplica
            drawerLayout.closeDrawers(); // Opcional: fecha a barra lateral ao clicar
        });

        itemPequeno.setOnClickListener(v -> {
            salvarEAplicarColunas(3); // Salva 3 colunas e aplica
            drawerLayout.closeDrawers(); // Opcional: fecha a barra lateral ao clicar
        });

        conteudoReferencia = findViewById(R.id.conteudo_referencia);
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

                    txtFala.setText(card.getFala());

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
    }    private void configurarGavetaInternaCard(LinearLayout header, LinearLayout conteudo, TextView seta, String textoPadrao, String textoExpandido) {
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
    private void verificarEPreencherBancoInicial() {
        // 1. Acessa a memória rápida do Android para ler a nossa "flag"
        android.content.SharedPreferences prefs = getSharedPreferences("FallaPrefs", MODE_PRIVATE);
        boolean isBancoInicializado = prefs.getBoolean("banco_inicializado", false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // 2. Só entra no bloco de inserção se for a PRIMEIRA VEZ que o app roda
            if (!isBancoInicializado) {
                List<ItemCard> lista = db.itemCardDao().buscarPorCategoria(CategoriaItem.PESSOAL);

                // Se realmente estiver vazio, faz a inserção inicial
                if (lista == null || lista.isEmpty()) {
                    db.itemCardDao().inserir(new ItemCard("Eu", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_myplaces)));
                    db.itemCardDao().inserir(new ItemCard("Você", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.button_onoff_indicator_on)));
                    db.itemCardDao().inserir(new ItemCard("Ajuda", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_help)));
                    db.itemCardDao().inserir(new ItemCard("Mais", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_add)));
                    db.itemCardDao().inserir(new ItemCard("Meu", CategoriaItem.PESSOAL, String.valueOf(android.R.drawable.ic_menu_myplaces)));

                    // 3. Salva a flag avisando que o banco já foi preenchido!
                    // Assim, na próxima vez que o app abrir, ele nunca mais vai repovoar o banco.
                    prefs.edit().putBoolean("banco_inicializado", true).apply();
                }
            }

            // 4. Sempre carrega os cards da interface (seja 5, 20 ou 0 se você tiver apagado todos)
            carregarTodasAsGavetas();
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
                            tts.speak(textoParaFalar, TextToSpeech.QUEUE_FLUSH, null, null);
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
        // Carrega a gaveta de favoritos (que criamos anteriormente)
        carregarCardsFavoritos();

        // Conecta a categoria ao Grid correto na interface
        carregarGridEspecifico(CategoriaItem.PESSOAL, findViewById(R.id.conteudo_pessoal));
        carregarGridEspecifico(CategoriaItem.COMIDAS, findViewById(R.id.conteudo_alimentos));
        carregarGridEspecifico(CategoriaItem.LAZER, findViewById(R.id.conteudo_lazer));
        carregarGridEspecifico(CategoriaItem.REFERENCIA, findViewById(R.id.conteudo_referencia));
        carregarGridEspecifico(CategoriaItem.APRENDIZADO, findViewById(R.id.conteudo_aprendizado));
    }

    // ========================================================
// SALVA E APLICA O TAMANHO DOS CARDS PERMANENTEMENTE
// ========================================================
    private void salvarEAplicarColunas(int quantidadeColunas) {
        // 1. Salva a escolha no SharedPreferences do Android
        android.content.SharedPreferences pref = getSharedPreferences("ConfigFalla", MODE_PRIVATE);
        pref.edit().putInt("quantidade_colunas", quantidadeColunas).apply();

        // 2. LIMPEZA PREVENTIVA: Remove os cards ANTES de encolher o Grid para evitar o crash!
        if (conteudoFavoritos != null) conteudoFavoritos.removeAllViews();
        if (conteudoPessoal != null) conteudoPessoal.removeAllViews();
        if (conteudoComidas != null) conteudoComidas.removeAllViews();
        if (conteudoLazer != null) conteudoLazer.removeAllViews();
        if (conteudoReferencia != null) conteudoReferencia.removeAllViews();
        if (conteudoAprendizado != null) conteudoAprendizado.removeAllViews();

        // 3. Com a "casa limpa", podemos mudar o número de colunas em segurança
        if (conteudoFavoritos != null) conteudoFavoritos.setColumnCount(quantidadeColunas);
        if (conteudoPessoal != null) conteudoPessoal.setColumnCount(quantidadeColunas);
        if (conteudoComidas != null) conteudoComidas.setColumnCount(quantidadeColunas);
        if (conteudoLazer != null) conteudoLazer.setColumnCount(quantidadeColunas);
        if (conteudoReferencia != null) conteudoReferencia.setColumnCount(quantidadeColunas);
        if (conteudoAprendizado != null) conteudoAprendizado.setColumnCount(quantidadeColunas);

        // 4. Manda o banco de dados trazer os cards e desenhá-los novamente no novo formato
        // Mas SÓ fazemos isso se o banco (db) já estiver inicializado, para evitar erro no onCreate
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
                CategoriaItem.REFERENCIA,
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