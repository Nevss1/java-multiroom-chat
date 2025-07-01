package arquivos;
public class Ex2 {
    public static void main(String[] args){
        String[] countries = new String[3];
        countries[0] = "Brazil";
        countries[1] = "Germany";

        int i = 0;
        while (countries[i] != null){
            System.out.println(countries[i]);
            i++;
        }
    }
}
