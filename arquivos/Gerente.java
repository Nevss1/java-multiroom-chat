public class Gerente extends Funcionario {
    private double bonus;

    public Gerente(String nome, double salarioBase, int matricula, double bonus) {
        super(nome, salarioBase, matricula);
        this.getBonus(bonus);
    }

    public double getBonus(double bonus) {
        return this.bonus = bonus;
    }

    @Override
    public double calcularSalarioFinal(){
        return this.salarioBase + this.bonus;
    }

    public void exibirDados() {
        super.exibirDados();
    }
}
