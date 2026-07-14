public class App {
    private boolean wantOtherPlay = true;

    public static void main(String[] args) throws Exception {
        printWelcome();

        GuessingGame.initGame();
    }

    private static void printWelcome() {
        System.out.println("Welcome to the Number Guessing Game!\n" + //
                        "I'm thinking of a number between 1 and 100.\n" + //
                        "You have 5 chances to guess the correct number.");
    }
}
