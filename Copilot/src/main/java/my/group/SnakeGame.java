package my.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/* SnakeGame class
* This class is a simple implementation of the classic Snake game.
* The game is played on a 10x10 board. The snake is represented by the character 'X', the food is represented by the character 'O', and empty cells are represented by the space character.
* The snake can be controlled using the arrow keys. The game ends when the snake collides with the wall or itself.

* @param BOARD_HEIGHT The height of the game board.
* @param BOARD_WIDTH The width of the game board.
* @param EMPTY_CELL The character representing an empty cell.
* @param SNAKE_CELL The character representing the snake.
* @param FOOD_CELL The character representing the food.
* @param snakeHead The position of the snake's head.
* @param snakeTail The position of the snake's tail.
* @param food The position of the food.
* @param snake A list containing the positions of all the snake segments.
* @param board A 2D array representing the game board.
* @return void

* @see <a href="
* @since 1.0
* */

public class SnakeGame {
    private static final int BOARD_HEIGHT = 10;
    private static final int BOARD_WIDTH = 10;
    private static final char EMPTY_CELL = ' ';
    private static final char SNAKE_CELL = 'X';
    private static final char FOOD_CELL = 'O';
    private static int[] snakeHead;
    private static int[] snakeTail;
    private static int[] food;
    private static List<int[]> snake;
    private static char[][] board;

    /*
    * Initializes the game by creating the snake, the food, and the game board.
    * @return void
     */
    public static void main(String[] args) throws InterruptedException {
        initGame();
        boolean gameOver = false;
        while (!gameOver) {
            //rendering the board
            renderBoard();
            char input = getUserInput();
            gameOver = updateGameState(input);
            TimeUnit.MILLISECONDS.sleep(100);
        }

        System.out.println("Game over!");
    }

    /*
    * Initializes the game by creating the snake, the food, and the game board.
    * @return void
     */
    private static void initGame() {
        snake = initSnake();
        food = initFood();
        board = initBoard(food);
    }

    /*
    * Initializes the game board by placing the food at the specified position.
    * @param food The position of the food.
     */
    private static char[][] initBoard(int[] food) {
        board = new char[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();
        board[food[0]][food[1]] = FOOD_CELL;
        return board;
    }

    /*
    * Initializes the position of the food.
    * @return int[] The position of the food.
     */
    private static int[] initFood() {
        return new int[]{BOARD_HEIGHT / 2, BOARD_WIDTH / 2};
    }

    /*
    * Initializes the snake by creating the head and tail segments.
    * @return List<int[]> A list containing the positions of all the snake segments.
    *
     */
    private static List<int[]> initSnake() {
        snake = new ArrayList<>();
        snakeHead = new int[]{BOARD_HEIGHT / 2, BOARD_WIDTH / 4};
        snakeTail = new int[]{BOARD_HEIGHT / 2, BOARD_WIDTH / 2};
        snake.add(snakeHead);
        snake.add(new int[]{snakeHead[0], snakeHead[1] - 1});
        snake.add(new int[]{snakeHead[0], snakeHead[1] - 2});
        return snake;
    }

    /*
    * Clears the game board by setting all cells to the empty character.
    * @return void
     */
    private static void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    /*
    * Gets the user input from the console.
    * @return char The user input.
     */
    private static char getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Use arrow keys to move the snake. Press Q to quit.");
        char input = scanner.next().charAt(0);
        return Character.toUpperCase(input);
    }

    /*
    * Moves the snake to the new position and updates the game state.
    * @param newHead The new position of the snake's head.
     */
    private static void moveSnake(int[] newHead) {
        if (isFood(newHead)) {
            spawnNewFood();
        } else {
            snakeTail = snake.remove(snake.size() - 1);
        }

        snake.add(0, newHead);
        snakeHead = newHead;
    }

    /*
    * Checks if the new position of the snake's head is occupied by the food.
    * @param newPos The new position of the snake's head.
     */
    private static boolean isFood(int[] newPos) {
        return newPos[0] == food[0] && newPos[1] == food[1];
    }

    /*
    * Spawns a new food at a random position on the game board.
    * @return void
     */
    private static void spawnNewFood() {
        Random random = new Random();
        food = null;
        while (food == null) {
            int[] newFood = {random.nextInt(BOARD_HEIGHT), random.nextInt(BOARD_WIDTH)};
            if (!isPositionOccupiedBySnake(newFood)) {
                food = newFood;
            }
        }
    }

    /*
    * Updates the game state based on the user input.
    * @param input The user input.
     */
    private static boolean updateGameState(char input) {
        int[] newHead = processSnakeMovement(input);
        if (!isValidMove(newHead)) {
            return true;
        }

        moveSnake(newHead);
        return false;
    }

    /*
    * Processes the user input and returns the new position of the snake's head.
    * @param input The user input.
     */
    private static int[] processSnakeMovement(char input) {
        int[] newHeadPosition = {snakeHead[0], snakeHead[1]};
        switch (input) {
            case 'W':
                newHeadPosition[0]--;
                break;
            case 'A':
                newHeadPosition[1]--;
                break;
            case 'S':
                newHeadPosition[0]++;
                break;
            case 'D':
                newHeadPosition[1]++;
                break;
        }
        return newHeadPosition;
    }

    /*
    * Checks if the new position of the snake's head is a valid move.
    * @param newPos The new position of the snake's head.
     */
    private static boolean isValidMove(int[] newPos) {
        if (newPos[0] < 0 || newPos[0] >= BOARD_HEIGHT || newPos[1] < 0 || newPos[1] >= BOARD_WIDTH) {
            return false;
        }

        return !isPositionOccupiedBySnake(newPos);
    }

    /*
    * Renders the game board to the console.
    * @return void
     */
    private static void renderBoard() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (i == snakeHead[0] && j == snakeHead[1]) {
                    board[i][j] = SNAKE_CELL;
                } else if (i == food[0] && j == food[1]) {
                    board[i][j] = FOOD_CELL;
                } else {
                    boolean isSnake = updateBoardForSnake(i, j);
                    if (!isSnake) {
                        board[i][j] = EMPTY_CELL;
                    }
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    /*
    * Updates the game board to reflect the position of the snake.
    * @param row The row of the game board.
     */
    private static boolean updateBoardForSnake(int row, int col) {
        for (int[] segment : snake) {
            if (row == segment[0] && col == segment[1]) {
                board[row][col] = SNAKE_CELL;
                return true;
            }
        }
        return false;
    }

    /*
    * Checks if the new position is occupied by the snake.
    * @param newPos The new position.
     */
    private static boolean isPositionOccupiedBySnake(int[] newPos) {
        for (int[] segment : snake) {
            if (newPos[0] == segment[0] && newPos[1] == segment[1]) {
                return true;
            }
        }
        return false;
    }
}