import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class App {
    public static void main(String[] args) {
        String caminhoArquivoCoordenadas = "../T2/src/CasosTeste/mapa30.txt";
        String caminhoArquivoArestas = "../T2/src/Arestas.txt";

        Map<String, String> portos = null;
        char[][] mapa = null;
        char[][] mapaCaminhos = null;
        double custoTotal = 0.0;

        try {
            mapa = entradaDados(caminhoArquivoCoordenadas);
            mapaCaminhos = new char[mapa.length][mapa[0].length];
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivoArestas))) {
            portos = gerarPortos(mapa, writer);
            gerarArestas(mapa, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        limparMatriz(mapaCaminhos);

        EdgeWeightedDigraph g = new EdgeWeightedDigraph(caminhoArquivoArestas);
        List<String> portosOrdenados = new ArrayList<>(portos.keySet());
        Collections.sort(portosOrdenados);

        String ultimoVisitado = "";

        System.out.println("Custos");
        for (int i = 0; i < portosOrdenados.size(); i++) {
            String origem = portosOrdenados.get(i);
            String destino = portosOrdenados.get((i + 1) % portosOrdenados.size());

            if(!ultimoVisitado.equals("") && !ultimoVisitado.equals(origem)){
                origem = ultimoVisitado;
            }

            Dijkstra dij = new Dijkstra(g, portos.get(origem));
            String rotuloDestino = portos.get(destino);

            if (!dij.hasPathTo(rotuloDestino)) {
                System.out.println("Porto inacessível: de " + origem + " para " + destino);
            } else {
                limparMatriz(mapaCaminhos);
                for (Edge e : dij.pathTo(rotuloDestino)) {
                    int linha = Integer.parseInt(e.getV().substring(0, e.getV().indexOf('x')));
                    int coluna = Integer.parseInt(e.getV().substring(e.getV().indexOf('x') + 1));
                    
                    if(e.getV().equals(portos.get(origem))){
                        mapaCaminhos[linha][coluna] = procurarChave(portos, e.getV());
                    } else if(e.getW().equals(portos.get(destino))){
                        mapaCaminhos[linha][coluna] = destino.charAt(0);
                    } else {
                        mapaCaminhos[linha][coluna] = '.';
                    }
                }
                System.out.println(origem + " -> " + destino + ": " + dij.getCombustivel());
                custoTotal += dij.getCombustivel();
                ultimoVisitado = destino;
                imprimirMatriz(mapaCaminhos);
            }
        }

        System.out.println("Custo total: " + custoTotal);
    }

    private static char[][] entradaDados(String caminhoArquivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
        String linha = br.readLine();

        String[] partes = linha.trim().split("\\s+");
        int linhas = Integer.parseInt(partes[0]);
        int colunas = Integer.parseInt(partes[1]);

        char[][] mapa = new char[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            linha = br.readLine();
            if (linha.length() < colunas) {
                linha = String.format("%-" + colunas + "s", linha).replace(' ', '.');
            } else if (linha.length() > colunas) {
                linha = linha.substring(0, colunas);
            }
            mapa[i] = linha.toCharArray();
        }

        br.close();
        return mapa;
    }

    private static Map<String, String> gerarPortos(char[][] mapa, BufferedWriter writer) throws IOException {
        Map<String, String> portos = new HashMap<String, String>();
        int linhas = mapa.length;
        int colunas = mapa[0].length;

        String porto;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (mapa[i][j] != '*' && mapa[i][j] != '.'){
                    porto = Character.toString(mapa[i][j]);
                    portos.put(porto, gerarRotulo(i, j));
                }
            }
        }
        return portos;
    }

    private static void gerarArestas(char[][] mapa, BufferedWriter writer) throws IOException {
        int linhas = mapa.length;
        int colunas = mapa[0].length;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {

                if (mapa[i][j] == '*') continue;

                String rotulo = gerarRotulo(i, j);

                if (i + 1 < linhas && mapa[i + 1][j] != '*') {
                    String rotuloAux = gerarRotulo(i + 1, j);
                    inserirArquivo(writer, rotulo, rotuloAux, 1.0);
                }
                if (i - 1 >= 0 && mapa[i - 1][j] != '*') {
                    String rotuloAux = gerarRotulo(i - 1, j);
                    inserirArquivo(writer, rotulo, rotuloAux, 1.0);
                }
                if (j + 1 < colunas && mapa[i][j + 1] != '*') {
                    String rotuloAux = gerarRotulo(i, j + 1);
                    inserirArquivo(writer, rotulo, rotuloAux, 1.0);
                }
                if (j - 1 >= 0 && mapa[i][j - 1] != '*') {
                    String rotuloAux = gerarRotulo(i, j - 1);
                    inserirArquivo(writer, rotulo, rotuloAux, 1.0);
                }
            }
        }
    }


    private static String gerarRotulo(int i, int j){
        return i + "x" + j;
    }

    private static void inserirArquivo(BufferedWriter writer, String rotulo1, String rotulo2, double peso) throws IOException {
        String linha = String.format("%s %s %.1f", rotulo1, rotulo2, peso);
        writer.write(linha);
        writer.newLine();
    }

    private static char[][] limparMatriz (char[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = ' ';
            }
        }
        return m;
    }
    
    private static void imprimirMatriz (char[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j]+ "");
            }
            System.out.println("");
        }
    }

    private static char procurarChave(Map<String, String> map, String value){
        for (String key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key.charAt(0);
            }
        }
        return ' ';
    }
}
