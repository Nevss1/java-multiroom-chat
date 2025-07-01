package arquivos;
public class Main {
    public static void main(String[] args){
        Triangulo triangulo = new Triangulo(3, "triangao", new float[]{3, 4, 5});
        triangulo.mostrarArea();
        triangulo.mostrarDimensoes();
    }
}
