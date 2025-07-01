package arquivos;
import java.util.Scanner;

public class Ex1{
   public static void main(String[] args){


    int 
    Scanner scanner = new Scanner(System.in);
    System.out.println("What day is it today?");
    String response = scanner.nextLine();

    scanner.close();

    switch(response){
        case "Sunday": System.out.println("It is Sunday!");
        break;
        case "Friday": System.out.println("It is Friday!");
        break;
        default: System.out.println("That is not a day!");
    }

   }
}