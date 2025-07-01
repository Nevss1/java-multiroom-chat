package arquivos;
public abstract class FiguraGeometrica {
    protected int numLados;
    protected String nome;
    protected float[] dimensoes;

    public FiguraGeometrica(int numLados, String nome, float[] dimensoes){
        this.numLados = numLados;
        this.nome = nome;
        this.dimensoes = dimensoes;
    }

    public float calcularPerimetro(){
        float perimetro = 0;
        for(int i = 0; i < dimensoes.length; i++){
            perimetro += dimensoes[i];
        }
        return perimetro;
    }

    public abstract float calcularArea();

    public void mostrarDimensoes(){
        for(int i = 0; i < dimensoes.length; i++){
            System.out.println(dimensoes[i]);
        }
    }
}
