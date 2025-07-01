package arquivos;
public class ExemploThreads {
    public static void main(String[] args){
        contadorCrescente c1 = new contadorCrecente();
        contadorDecrescente c2 = new contadorDecrescente();
        System.out.println("Sem Thread: ");
        c1.run();
        c2.run();

        System.out.println("Com Thread: ");
        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);

        t1.start();
        t2.start();
    }
}
