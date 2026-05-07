import java.util.NoSuchElementException;

public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;
	
	Fila() {
		
		Celula<E> sentinela = new Celula<E>();
		frente = tras = sentinela;
	}
	
	public boolean vazia() {
		
		return (frente == tras);
	}
	
	public void enfileirar(E item) {
		
		Celula<E> novaCelula = new Celula<E>(item);
		
		tras.setProximo(novaCelula);
		tras = tras.getProximo();
	}
	
	public E desenfileirar() {
		
		E item = null;
		Celula<E> primeiro;
		
		item = consultarPrimeiro();
		
		primeiro = frente.getProximo();
		frente.setProximo(primeiro.getProximo());
		
		primeiro.setProximo(null);
			
		
		if (primeiro == tras)
			tras = frente;
		
		return item;
	}
	
	public E consultarPrimeiro() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na fila!");
		}

		return frente.getProximo().getItem();

	}
    public int contarCaracteres(E letra){
        if (vazia()) {
            throw new NoSuchElementException("Nao há nenhum item na fila!");
        }
        Celula<E> atual = frente.getProximo();
        int contador = 0;
        while (atual != null) {
            E item = atual.getItem();
                if (item.equals(letra)) {
                    contador++;
                }
            
            atual = atual.getProximo();
        }
        return contador;
    }
	
	/**
	 * Desenfileira os primeiros K elementos (definidos por numItens) da fila atual,
	 * respeitando a ordem de chegada, e retorna esses elementos estruturados em uma
	 * nova Fila flexível. Caso a fila original possua menos de K itens, o método deve
	 * extrair apenas os itens disponíveis, esvaziando a fila de origem.
	 *
	 * @param numItens o número de itens a serem extraídos
	 * @return uma nova Fila flexível contendo os itens extraídos
	 */
	public Fila<E> extrairLote(int numItens) {
		if (numItens < 0) {
			throw new IllegalArgumentException("O número de itens não pode ser negativo.");
		}
		
		Fila<E> lote = new Fila<>();
		int extraidos = 0;
		
		while (!this.vazia() && extraidos < numItens) {
			lote.enfileirar(this.desenfileirar());
			extraidos++;
		}
		
		return lote;
	}

	public void imprimir() {
		
		Celula<E> aux;
		
		if (vazia())
			System.out.println("A fila está vazia!");
		else {
			aux = this.frente.getProximo();
			while (aux != null) {
				System.out.println(aux.getItem());
				aux = aux.getProximo();
			}
		} 	
	}
}