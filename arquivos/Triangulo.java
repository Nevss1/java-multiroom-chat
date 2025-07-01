package arquivos;
public class Triangulo extends FiguraGeometrica {
    public Triangulo(int numLados, String nome, float[] dimensoes){
        super(numLados, nome, dimensoes);
    }
    
    @Override
    public float calcularArea(){
        float s = calcularPerimetro()/2;
        float a = dimensoes[0];
        float b = dimensoes[1];
        float c = dimensoes[2];
        return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    public void mostrarArea(){
        System.out.println("A área do triângulo é " + this.calcularArea());
    }
}
