import java.util.Scanner;

class Main {
  public static void main(String[] args) {
    Scanner keyboard = new Scanner(System.in);

    // Give instructions and limiations
    System.out.print("NOTE: Enter bases within domain of [2, 72]. Supports up to 10 decimal places fairly accuratly.\n");

    System.out.print("Enter your input: ");
    String input = keyboard.next();

    System.out.print("Enter the base of the input: ");
    int inputBase = keyboard.nextInt();

    System.out.print("Enter what base you want the output to be: ");
    int outputBase = keyboard.nextInt();

    System.out.println("\nHere is your new number: " + Radix.changeBase(input, inputBase, outputBase));
    keyboard.close();
  }
}
