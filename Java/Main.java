import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.Scanner;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Change the second argument to false to overwrite the log file with every run
        FileHandler fileHandler = new FileHandler("game_results.log", false);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter player type (random or user):");
        String playerType = scanner.nextLine();
        System.out.println("Enter minimax type (stock or new (for the version with the reuristic function) ):");
        String minimaxType = scanner.nextLine();
        System.out.println("Enter number of simulations to run:");
        int numberOfSimulations = scanner.nextInt();

        EvaluationFunction evaluationFunction;
        if (minimaxType.equals("stock")) {
            evaluationFunction = new StockMinimax();
        } else {
            evaluationFunction = new NewMinimax();
        }

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Character>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfSimulations; i++) {
            futures.add(executor.submit(new Callable<Character>() {
                @Override
                public Character call() throws Exception {
                    ReversiGame game = new ReversiGame(evaluationFunction);
                    game.playGame(playerType, "minimax"); // Adjust the player types as needed
                    logger.info("Algorithm used: " + evaluationFunction.getClass().getSimpleName());
                    char winner = game.getWinner();
                    logger.info("Game ended. Winner: " + (winner != '-' ? winner : "Tie"));
                    logger.info(""); // This will create an empty line in the log file
                    return winner;
                }
            }));
        }

        executor.shutdown();
    }
}