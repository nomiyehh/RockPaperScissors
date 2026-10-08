import java.util.Random;
import java.util.Scanner;

public class RockPaperScissors {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        String[] moves = {"rock", "paper", "scissors"};
        int userScore = 0;
        int computerScore = 0;

        System.out.println("=================================");
        System.out.println(" Welcome to Rock, Paper, Scissors!");
        System.out.println(" Type 'rock', 'paper', or 'scissors'.");
        System.out.println(" Type 'quit' anytime to end the game.");
        System.out.println("=================================");

        while (true) {
            System.out.print("\nYour move: ");
            String userMove = scanner.nextLine().trim().toLowerCase();

            if (userMove.equals("quit")) {
                break;
            }

            // Input validation
            if (!userMove.equals("rock") && !userMove.equals("paper") && !userMove.equals("scissors")) {
                System.out.println("Invalid choice. Please enter rock, paper, scissors, or quit.");
                continue;
            }

            // Generate computer move (0 = rock, 1 = paper, 2 = scissors)
            int computerIndex = random.nextInt(3);
            String computerMove = moves[computerIndex];

            System.out.println("Computer chose: " + computerMove);

            // Determine winner
            if (userMove.equals(computerMove)) {
                System.out.println("It's a tie!");
            } else if (
                (userMove.equals("rock") && computerMove.equals("scissors")) ||
                (userMove.equals("paper") && computerMove.equals("rock")) ||
                (userMove.equals("scissors") && computerMove.equals("paper"))
            ) {
                System.out.println("You win this round!");
                userScore++;
            } else {
                System.out.println("Computer wins this round!");
                computerScore++;
            }

            System.out.printf("Score -> You: %d | Computer: %d\n", userScore, computerScore);
        }

        System.out.println("\n--- Final Score ---");
        System.out.printf("You: %d | Computer: %d\n", userScore, computerScore);
        if (userScore > computerScore) {
            System.out.println("Congratulations! You won the match!");
        } else if (computerScore > userScore) {
            System.out.println("Better luck next time! Computer won the match!");
        } else {
            System.out.println("The match ended in a draw!");
        }

        scanner.close();
    }
}
