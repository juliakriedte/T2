public class Nodo {
    int x, y;

    public Nodo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Importante para usar como chave em Map ou Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nodo)) return false;
        Nodo nodo = (Nodo) o;
        return x == nodo.x && y == nodo.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
