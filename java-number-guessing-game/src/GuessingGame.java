import java.util.Random;
import java.util.Scanner;

public class GuessingGame {
    private Scanner input;
    private int chances;
    private int number;
    private GuessingGame.Difficulty difficulty;
    private boolean win = false;

    private enum Difficulty {
        EASY(10),
        MEDIUM(5),
        HARD(3);

        private int chances;

        private Difficulty(int chances) {
            this.chances = chances;
        }
    }

    private GuessingGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        chances = difficulty.chances;
        input = new Scanner(System.in);
        number = new Random().nextInt(100);
    }

    public static void initGame() {
        var wantAgain = false;
        do {
            GuessingGame game = new GuessingGame(requestDifficulty());
            game.play();
            game.input.nextLine();
            System.err.print("Game Over! Want to play again?(s/n) (default: n)");
            var opt = game.input.nextLine();
            wantAgain = (opt.equals("s"))? true : false;
        } while(wantAgain);
        
    }

    private static Difficulty requestDifficulty() {
        Scanner inputTemp = new Scanner(System.in);
        System.out.println("\n" + //
                        "Please select the difficulty level:\n" + //
                        "1. Easy (10 chances)\n" + //
                        "2. Medium (5 chances)\n" + //
                        "3. Hard (3 chances)");

        var messageChoice = "Great! You have selected the %s difficulty level.";
        while(true) {
            System.out.print("\nEnter your choice: ");
            int opt = inputTemp.nextInt();
            switch (opt) {
                case 1:
                    System.out.println(String.format(messageChoice, "Easy"));
                    return Difficulty.EASY;
                case 2:
                    System.out.println(String.format(messageChoice, "Medium"));
                    return Difficulty.MEDIUM;
                case 3:
                    System.out.println(String.format(messageChoice, "Hard"));
                    return Difficulty.HARD;
                default:
                    System.out.println("Invalid Option! Try Again!");
            }
        }
    }

    private void play() {
        while (chances > 0 && !win) {
            System.out.print("Enter your guess: ");
            int attempt = input.nextInt();
            verify(attempt);
        }
    }

    private void verify(int attempt) {
        chances -= 1;
        if (attempt == number) {
            win = true;
            System.out.println("Congratulations! You guessed the correct number in " + (difficulty.chances - chances) + " attempts.");
        } else if (attempt < number) {
            System.out.println("Incorrect! The number is greater than " + attempt + ".");
        } else {
            System.out.println("Incorrect! The number is less than " + attempt + ".");
        }
    }
}
