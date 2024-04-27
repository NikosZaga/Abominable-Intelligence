import java.util.*;

public class ReversiGame {
    private static final int BOARD_SIZE = 8;
    private char[][] board;
    private char currentPlayer;
    private char opponent;

    private EvaluationFunction evaluationFunction;

    public ReversiGame(EvaluationFunction evaluationFunction) {
        this.evaluationFunction = evaluationFunction;
        board = new char[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 'X';
        opponent = 'O';
        initializeBoard();
    }


    public ReversiGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 'X';
        opponent = 'O';
        initializeBoard();
    }


    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '-';
            }
        }
        // Initial pieces
        board[3][3] = 'X';
        board[4][4] = 'X';
        board[3][4] = 'O';
        board[4][3] = 'O';
    }

    private void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private boolean isValidMove(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != '-') {
            return false;
        }
        // Check if move flips any pieces
        boolean valid = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                valid |= checkDirection(row, col, i, j);
            }
        }
        return valid;
    }

    private boolean checkDirection(int row, int col, int dx, int dy) {
        int r = row + dx;
        int c = col + dy;
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE || board[r][c] == '-' || board[r][c] == currentPlayer) {
            return false;
        }
        while (board[r][c] == opponent) {
            r += dx;
            c += dy;
            if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) {
                return false;
            }
        }
        return board[r][c] == currentPlayer && (r != row + dx || c != col + dy);
    }

    private void makeMove(int row, int col) {
        if (!isValidMove(row, col)) {
            System.out.println("Invalid move! Try again.");
            return;
        }
        board[row][col] = currentPlayer;
        // Flip pieces
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                flipPieces(row, col, i, j);
            }
        }
        // Switch player
        char temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;
    }

    private void flipPieces(int row, int col, int dx, int dy) {
        int r = row + dx;
        int c = col + dy;
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == opponent) {
            board[r][c] = currentPlayer;
            r += dx;
            c += dy;
        }
    }

    private List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    private boolean isGameOver() {
        return getValidMoves().isEmpty();
    }

    char getWinner() {
        int xCount = 0;
        int oCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'X') {
                    xCount++;
                } else if (board[i][j] == 'O') {
                    oCount++;
                }
            }
        }
        if (xCount > oCount) {
            return 'X';
        } else if (oCount > xCount) {
            return 'O';
        } else {
            return '-';
        }
    }

    int evaluate() {
        int xCount = 0;
        int oCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'X') {
                    xCount++;
                } else if (board[i][j] == 'O') {
                    oCount++;
                }
            }
        }
        return xCount - oCount;
    }


    private int minimax(int depth, boolean maximizingPlayer) {
        if (depth == 0 || isGameOver()) {
            return evaluationFunction.evaluate(this);
        }
        List<int[]> validMoves = getValidMoves();
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : validMoves) {
                int row = move[0];
                int col = move[1];
                ReversiGame cloneGame = clone();
                cloneGame.makeMove(row, col);
                int eval = cloneGame.minimax(depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : validMoves) {
                int row = move[0];
                int col = move[1];
                ReversiGame cloneGame = clone();
                cloneGame.makeMove(row, col);
                int eval = cloneGame.minimax(depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    protected ReversiGame clone() {
        ReversiGame cloneGame = new ReversiGame(this.evaluationFunction);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cloneGame.board[i][j] = this.board[i][j];
            }
        }
        cloneGame.currentPlayer = this.currentPlayer;
        cloneGame.opponent = this.opponent;
        return cloneGame;
    }

    private int[] minimaxMove(int depth) {
        List<int[]> validMoves = getValidMoves();
        int bestMoveIndex = -1;
        int bestEval = Integer.MIN_VALUE;
        for (int i = 0; i < validMoves.size(); i++) {
            int[] move = validMoves.get(i);
            int row = move[0];
            int col = move[1];
            ReversiGame cloneGame = clone();
            cloneGame.makeMove(row, col);
            int eval = cloneGame.minimax(depth, false);
            if (eval > bestEval) {
                bestEval = eval;
                bestMoveIndex = i;
            }
        }
        return validMoves.get(bestMoveIndex);
    }

    private int[] randomMove() {
        List<int[]> validMoves = getValidMoves();
        Random rand = new Random();
        return validMoves.get(rand.nextInt(validMoves.size()));
    }

    public void playGame(String playerType, String aiType) {
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver()) {
            printBoard();
            System.out.println("Current player: " + currentPlayer);
            if (currentPlayer == 'X') {
                if (aiType.equals("minimax")) {
                    int[] move = minimaxMove(3); // Adjust depth as needed
                    makeMove(move[0], move[1]);
                } else if (aiType.equals("random")) {
                    int[] move = randomMove();
                    makeMove(move[0], move[1]);
                }
            } else {
                if (playerType.equals("human")) {
                    System.out.println("Enter your move (row col): ");
                    int row = scanner.nextInt();
                    int col = scanner.nextInt();
                    makeMove(row, col);
                } else if (playerType.equals("minimax")) {
                    int[] move = minimaxMove(3); // Adjust depth as needed
                    makeMove(move[0], move[1]);
                } else if (playerType.equals("random")) {
                    int[] move = randomMove();
                    makeMove(move[0], move[1]);
                }
            }
        }
        printBoard();
        char winner = getWinner();
        if (winner != '-') {
            System.out.println("Player " + winner + " wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
    private static final int[][] WEIGHTS = {
            {20, -3, 11, 8, 8, 11, -3, 20},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {20, -3, 11, 8, 8, 11, -3, 20}
    };

    public int heuristic() {
        int tiles = calculateTiles();
        int corners = calculateCorners();
        int proximity = calculateProximity();
        int mobility = calculateMobility();
        int discs = calculateDiscs();

        int score = 10 * tiles + 801_724 * corners + 382_026 * proximity + 78_922 * mobility + 10 * discs;

        return currentPlayer == 'X' ? score : -score;
    }

    private int calculateTiles() {
        int tilesX = 0;
        int tilesO = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'X') {
                    tilesX++;
                } else if (board[i][j] == 'O') {
                    tilesO++;
                }
            }
        }
        if (tilesX > tilesO) {
            return 100 * tilesX / (tilesX + tilesO);
        } else if (tilesX < tilesO) {
            return -100 * tilesO / (tilesX + tilesO);
        } else {
            return 0;
        }
    }

    private int calculateCorners() {
        int cornersX = 0;
        int cornersO = 0;
        int[] cornerPositions = {0, BOARD_SIZE - 1};
        for (int i : cornerPositions) {
            for (int j : cornerPositions) {
                if (board[i][j] == 'X') {
                    cornersX++;
                } else if (board[i][j] == 'O') {
                    cornersO++;
                }
            }
        }
        return 25 * (cornersX - cornersO);
    }

    private int calculateProximity() {
        int proximityX = 0;
        int proximityO = 0;
        int[][] proximityPositions = {{0, 0}, {0, BOARD_SIZE - 1}, {BOARD_SIZE - 1, 0}, {BOARD_SIZE - 1, BOARD_SIZE - 1}};
        for (int[] pos : proximityPositions) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int x = pos[0] + dx;
                    int y = pos[1] + dy;
                    if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
                        if (board[x][y] == 'X') {
                            proximityX++;
                        } else if (board[x][y] == 'O') {
                            proximityO++;
                        }
                    }
                }
            }
        }
        return -12_5 * (proximityX - proximityO);
    }

    private int calculateMobility() {
        int movesX = getValidMoves('X').size();
        int movesO = getValidMoves('O').size();
        if (movesX > movesO) {
            return 100 * movesX / (movesX + movesO);
        } else if (movesX < movesO) {
            return -100 * movesO / (movesX + movesO);
        } else {
            return 0;
        }
    }

    private int calculateDiscs() {
        int discsX = 0;
        int discsO = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'X') {
                    discsX += WEIGHTS[i][j];
                } else if (board[i][j] == 'O') {
                    discsO += WEIGHTS[i][j];
                }
            }
        }
        return discsX - discsO;
    }

    private List<int[]> getValidMoves(char player) {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j, player)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    private boolean isValidMove(int row, int col, char player) {
        // Add your implementation here
        return false;
    }


}

