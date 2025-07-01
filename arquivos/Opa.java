package arquivos;
import java.util.ArrayList;

import java.util.Set;
import java.util.HashSet;

import java.util.Map;
import java.util.HashMap;

public class Opa {
    public static void main(String[] args){{
        ArrayList<String> frutas = new ArrayList<>();

        frutas.add("Maça");
        frutas.add("Pera");

        frutas.set(0, "Laranja"); // substitui
        System.out.println(frutas.get(0)); // Laranja

        System.out.println(frutas.contains("Maça")); // false



        Set<String> nomes = new HashSet<>(); // comportamento em conjunto
        nomes.add("João");
        nomes.add("Ana");
        nomes.add("João"); // duplicado, nn aparece

        for (String nome : nomes){
            System.out.println(nome);
        }
        nomes.contains("João"); // True



        Map<String, Integer> mapa = new HashMap<>();

        mapa.put("Ana", 25);
        mapa.put("Ronaldo", 20);

        mapa.get("Ana"); // 25
        mapa.containsKey("Carlos"); // false
        mapa.remove("Ana"); 
    }}
}
