public abstract class Funcionario{
    protected String nome;
    protected double salarioBase;
    protected int matricula;

    public Funcionario(String nome, double salarioBase, int matricula){
        this.nome = nome;
        this.salarioBase = salarioBase;
        this.matricula = matricula;
    }

    public abstract double calcularSalarioFinal();

    public void exibirDados(){
        System.out.println(nome);
        System.out.println(matricula);
        System.out.println(salarioBase);
    };
}