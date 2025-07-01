public class Comissionado extends Funcionario {
    private double totalVendas;
    private float comissao; // entre 0 e 1

    public Comissionado(String nome, double salarioBase, int matricula, double totalVendas,  float comissao) {
        super(nome, salarioBase, matricula);
        this.getTotalVendas(totalVendas);
        this.getComissao(comissao);
    }

    public double getTotalVendas(double totalVendas) {
        return this.totalVendas = totalVendas;
    }

    public float getComissao(float comissao) {
        return this.comissao = comissao;
    }

    @Override
    public double calcularSalarioFinal(){
        return this.salarioBase + (this.totalVendas * this.comissao);
    }

    @Override
    public void exibirDados() {
        super.exibirDados();
    }
}
