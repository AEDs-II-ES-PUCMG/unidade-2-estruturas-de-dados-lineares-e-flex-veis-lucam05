import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();
        
    /** Pilha de produtos mais recentemente pedidos */
    static Pilha<Produto> pilhaProdutosRecentes = new Pilha<>();

    /** Fila de pedidos aguardando processamento */
    static Fila<Pedido> filaPedidos = new Fila<>();

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Testar Pilha");
        System.out.println("8 - Testar Fila ");
        System.out.println("9 - Processar Lote de Pedidos ");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado em uma pilha de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
    	
    	if (pedido == null) {
    		System.out.println("Não há um pedido em andamento para ser finalizado.");
    		return;
    	}
    	
    	pilhaPedidos.empilhar(pedido);
    	filaPedidos.enfileirar(pedido);
    	
    	ItemDePedido[] itens = pedido.getItensDoPedido();
    	for (ItemDePedido item : itens) {
    		if (item != null) {
    			pilhaProdutosRecentes.empilhar(item.getProduto());
    		}
    	}
    	
    	System.out.println("Pedido finalizado, inserido na fila e produtos armazenados com sucesso!");
    }
    
    public static void listarProdutosPedidosRecentes() {
    	
    	Integer numItens = lerOpcao("Quantos produtos recentes você deseja visualizar?", Integer.class);
    	if (numItens == null) {
    		System.out.println("Opção inválida.");
    		return;
    	}
    	
    	try {
    		Pilha<Produto> recentes = pilhaProdutosRecentes.subPilha(numItens);
    		System.out.println("\n--- " + numItens + " Produtos Mais Recentes ---");
    		recentes.imprimir();
    	} catch (IllegalArgumentException e) {
    		System.out.println("Erro: " + e.getMessage());
    	}
    }
    
    /**
     * Método para testar a pilha flexível com os dígitos da matrícula.
     * Insere os dígitos únicos da matrícula na pilha e testa os métodos
     * empilhar, desempilhar e consultarTopo.
     */
    public static void testarPilha() {
    	
    	// Número de matrícula para teste (modifique conforme necessário)
    	String matricula = "766211";
    	
    	System.out.println("\n========== TESTE PRELIMINAR DA PILHA ==========");
    	System.out.println("Matrícula: " + matricula);
    	System.out.println("============================================\n");
    	
    	// Criar pilha de Integer
    	Pilha<Integer> pilha = new Pilha<>();
    	
    	// Array para rastrear dígitos já inseridos (0-9)
    	boolean[] digitos = new boolean[10];
    	
    	// Empilhar dígitos únicos da matrícula
    	System.out.println("1. EMPILHANDO DÍGITOS ÚNICOS DA MATRÍCULA:");
    	for (int i = 0; i < matricula.length(); i++) {
    		int digito = Character.getNumericValue(matricula.charAt(i));
    		
    		// Verificar se o dígito já foi inserido
    		if (!digitos[digito]) {
    			pilha.empilhar(digito);
    			digitos[digito] = true;
    			System.out.println("   Empilhado: " + digito);
    		} else {
    			System.out.println("   Dígito " + digito + " já estava na pilha (repetido, não inserido)");
    		}
    	}
    	
    	// Imprimir conteúdo da pilha
    	System.out.println("\n2. IMPRIMINDO PILHA (do topo para o fundo):");
    	pilha.imprimir();
        System.out.println("\n3. IMPRIMINDO PILHA COM ORDEM INVERSA (do fundo para o topo):");
        pilha.imprimir_certo();
    	
        System.out.println("\n4. TESTANDO DESEMPILHAR:");
        int desempilhado = pilha.desempilhar();
        System.out.println("   Item desempilhado: " + desempilhado);
        System.out.println("   Novo topo da pilha: " + pilha.consultarTopo());
        
        System.out.println("\n5. ESVAZIANDO A PILHA:");
        while (!pilha.vazia()) {
            System.out.println("   Desempilhado: " + pilha.desempilhar());
        }
        
    	// Teste de exceção
    	System.out.println("\n6. TESTANDO EXCEÇÃO (consultarTopo em pilha vazia):");
    	try {
    		pilha.consultarTopo();
    	} catch (NoSuchElementException e) {
    		System.out.println("   Exceção capturada corretamente: " + e.getMessage());
    	}
    	
    	System.out.println("\n========== FIM DO TESTE ==========\n");
    }
    
    /**
     * Método para testar a fila flexível com os caracteres do nome (Tarefa 1).
     */
    public static void testarFila() {
    	
    	String nome = "Luca Monteiro";
    	
    	System.out.println("\n========== TESTE PRELIMINAR DA FILA ==========");
    	System.out.println("Nome: " + nome);
    	System.out.println("============================================\n");
    	
    	Fila<Character> fila = new Fila<>();
    	
    	System.out.println("1. ENFILEIRANDO CARACTERES DO NOME:");
    	for (int i = 0; i < nome.length(); i++) {
    		char c = nome.charAt(i);
    		fila.enfileirar(c);
    		System.out.println("   Enfileirado: " + c);
    	}
    	
    	System.out.println("\n2. IMPRIMINDO FILA:");
    	fila.imprimir();
    	
        char charBusca = 'o';
        System.out.println("\n3. CONTANDO OCORRÊNCIAS DO CARACTERE '" + charBusca + "':");
        int ocorrencias = fila.contarCaracteres(charBusca);
        System.out.println("   Ocorrências: " + ocorrencias);
        
        System.out.println("\n4. TESTANDO DESENFILEIRAR:");
        char desenfileirado = fila.desenfileirar();
        System.out.println("   Item desenfileirado: " + desenfileirado);
        System.out.println("   Novo primeiro da fila: " + fila.consultarPrimeiro());
        
        System.out.println("\n5. ESVAZIANDO A FILA:");
        while (!fila.vazia()) {
            System.out.println("   Desenfileirado: " + fila.desenfileirar());
        }
        
    	System.out.println("\n========== FIM DO TESTE ==========\n");
    }

    public static void processarLotePedidos() {
    	
        Integer numItens = lerOpcao("Quantos pedidos deseja extrair do lote para processamento?", Integer.class);
        if (numItens == null) {
            System.out.println("Opção inválida.");
            return;
        }
        
        try {
            Fila<Pedido> lote = filaPedidos.extrairLote(numItens);
            System.out.println("\n--- Lote de Pedidos Extraídos ---");
            lote.imprimir();
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                    finalizarPedido(pedido);
                    pedido = null;
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> testarPilha();
                case 8 -> testarFila();
                case 9 -> processarLotePedidos();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
