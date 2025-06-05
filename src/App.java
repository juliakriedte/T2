import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class App {
    public static void main(String[] args) {
        String caminhoArquivoCoordenadas = "../T2/src/Coordenadas.txt";
        String caminhoArquivoArestas = "../T2/src/Arestas.txt";
        Map<String, String> portos = null;
        char[][] mapa = null;

        try {
            mapa = entradaDados(caminhoArquivoCoordenadas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivoArestas))) {
            portos = gerarPortos(mapa, writer);
            gerarArestas(mapa, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EdgeWeightedDigraph g = new EdgeWeightedDigraph(caminhoArquivoArestas);
        Dijkstra dij = new Dijkstra(g, portos.get("1"));

       for (Edge e : dij.pathTo(portos.get("2"))) {
            System.out.print(e + " ");
        }
        System.out.println("- " + dij.distTo(portos.get("2")));
        System.err.println("Combustivel gasto total: "+dij.getCombustivel());

        // for (String v : g.getVerts()) {
        //     System.out.print(v + ": ");
        //     if (!dij.hasPathTo(v))
        //         System.out.println("Sem caminho!");
        //     else {
        //         for (Edge e : dij.pathTo(v)) {
        //             System.out.print(e + " ");
        //         }
        //         System.out.println("- " + dij.distTo(v));
        //     }
        // }
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

}
