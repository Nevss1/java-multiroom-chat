package arquivos;
public class Conta {
    private String titular;
    private double saldo;

    public Conta(String nome, double valor){
        this.setTitular(nome);
        this.setSaldo(valor);
    }

    public void setTitular(String titular){
        this.titular = titular;
    }

    public String getTitular(){
        return this.titular;
    }
    
    public void setSaldo(double saldo){
        this.saldo = saldo;
    }

    public Double getSaldo(){
        return this.saldo;
    }
}
