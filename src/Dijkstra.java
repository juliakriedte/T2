import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Dijkstra {

    private Map<String, Edge> edgeTo;
    private Map<String, Double> distTo;
    private Map<String, Direcoes> dirTo;
    private IndexMinHeap<String, Double> pq;
    public static double combustivel;

    public Dijkstra(EdgeWeightedDigraph g, String s) {
        edgeTo = new HashMap<>();
        distTo = new HashMap<>();
        dirTo = new HashMap<>();
        pq = new IndexMinHeap<>();
        combustivel = 0;

        for (String v : g.getVerts()){
            distTo.put(v, Double.POSITIVE_INFINITY);
            dirTo.put(v, Direcoes.INICIAL);
        }
        distTo.put(s, 0.0);

        pq.insert(s, 0.0);
        while (!pq.isEmpty()) {
            String v = pq.delMin();
            // System.out.println("Processando: " + v);
            for (Edge e : g.getAdj(v)) {
                relax(e);
            }
        }
    }

    public double getCombustivel(){
        return combustivel;
    }

    public boolean hasPathTo(String v) {
        Double dist = distTo.get(v);
        return dist != null && dist != Double.POSITIVE_INFINITY;

    }

    public double distTo(String v) {
        if (!hasPathTo(v))
            throw new UnsupportedOperationException("Sem caminho para " + v + "!");
        return distTo.get(v);
    }

    public Iterable<Edge> pathTo(String v) {
        LinkedList<Edge> path = new LinkedList<>();
        combustivel = 0;

        if (!hasPathTo(v))
            return path;

        Edge e = edgeTo.get(v);
        Direcoes direcaoAnterior = Direcoes.INICIAL;

        while (e != null) {
            path.addFirst(e);
            combustivel += e.getWeight();

            Direcoes direcaoAtual = descobrirDirecao(e.getV(), e.getW());
            if (direcaoAnterior != Direcoes.INICIAL && direcaoAnterior != direcaoAtual) {
                combustivel += 2; 
            }
            direcaoAnterior = direcaoAtual;

            e = edgeTo.get(e.getV());
        }
        return path;
    }


    private void relax(Edge e) {
        String v = e.getV();
        String w = e.getW();

        Direcoes direcaoAtual = dirTo.get(v);
        Direcoes proxDirecao = descobrirDirecao(v, w);
        int penalidade = (direcaoAtual != Direcoes.INICIAL && direcaoAtual != proxDirecao) ? 2 : 0;
        double calcularDistManhattan = calcularDistManhattan(v, w);
        double edgeWeight = distTo.get(v) + e.getWeight() + penalidade + calcularDistManhattan;
        
        if (distTo.get(w) + penalidade > edgeWeight) {
            // Achei um caminho melhor!
            distTo.put(w, edgeWeight);
            edgeTo.put(w, e);
            dirTo.replace(v, direcaoAtual);
            dirTo.replace(w, proxDirecao);
            if (!pq.contains(w))
                // Não existe, insere na fila
                pq.insert(w, edgeWeight);
            else
                // Já existe, reduz a prioridade (valor)
                pq.decreaseValue(w, edgeWeight);
        }
    }

    private Direcoes descobrirDirecao(String v, String w){
        int linhaV = Integer.parseInt(v.substring(0, v.indexOf('x')));
        int colunaV = Integer.parseInt(v.substring(v.indexOf('x') + 1));
        int linhaW = Integer.parseInt(w.substring(0, w.indexOf('x')));
        int colunaW = Integer.parseInt(w.substring(w.indexOf('x') + 1));

        if (linhaV > linhaW) {
            return Direcoes.NORTE;
        } else if (linhaV < linhaW) {
            return Direcoes.SUL;
        } else if (colunaV > colunaW) {
            return Direcoes.OESTE;
        } else if (colunaV < colunaW) {
            return Direcoes.LESTE;
        }
        return Direcoes.INICIAL;
    }

    private double calcularDistManhattan(String atual, String destino) {
        int linhaA = Integer.parseInt(atual.substring(0, atual.indexOf('x')));
        int colunaA = Integer.parseInt(atual.substring(atual.indexOf('x') + 1));
        int linhaD = Integer.parseInt(destino.substring(0, destino.indexOf('x')));
        int colunaD = Integer.parseInt(destino.substring(destino.indexOf('x') + 1));

        return Math.abs(linhaA - linhaD) + Math.abs(colunaA - colunaD);
    }

}