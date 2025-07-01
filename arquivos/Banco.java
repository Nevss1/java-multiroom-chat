package arquivos;

public class Banco{
    private String nomeBanco;
    private Conta conta;

    public Banco(String nome, Conta contaa){
        this.setNomeBanco(nome);
        this.setConta(contaa);
    }

    public void setNomeBanco(String nomeBanco){
        this.nomeBanco = nomeBanco;
    }
    
    public String getNomeBanco(){
        return this.nomeBanco;
    }

    public void setConta(Conta conta){
        this.conta = conta;
    }

    public Conta getConta(){
        return this.conta;
    }
}