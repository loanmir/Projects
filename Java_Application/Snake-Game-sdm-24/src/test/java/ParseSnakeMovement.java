import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ParseSnakeMovement {

    @Test
    void moveUpNoEatingHeadUpdate() {
        Board board = new Board("");
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.UP);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(4,5));
        assertEquals(Cell.BLANK, board.getCell(5,5));
        assertEquals(1, snake.getCoordSnake().size());
    }

    @Test
    void moveUpEatingHeadUpdate() {
        Board board = new Board("");
        board.setCell(5,6, Cell.BLANK);
        board.setCell(4,5, Cell.FOOD);
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.UP);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,5));
        assertEquals(Cell.SNAKE, board.getCell(4,5));
        assertEquals(2, snake.getCoordSnake().size());
    }

    @Test
    void moveDownNoEatingHeadUpdate() {
        Board board = new Board("");
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.DOWN);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(6,5));
        assertEquals(Cell.BLANK, board.getCell(5,5));
        assertEquals(1, snake.getCoordSnake().size());
    }

    @Test
    void moveDownEatingHeadUpdate() {
        Board board = new Board("");
        board.setCell(5,6, Cell.BLANK);
        board.setCell(6,5, Cell.FOOD);
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.DOWN);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,5));
        assertEquals(Cell.SNAKE, board.getCell(6,5));
        assertEquals(2, snake.getCoordSnake().size());
    }

    @Test
    void moveLeftNoEatingHeadUpdate() {
        Board board = new Board("");
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.UP);
        snakeMovement.moveSnake(Direction.LEFT);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,4));
        assertEquals(Cell.BLANK, board.getCell(5,5));
        assertEquals(1, snake.getCoordSnake().size());
    }

    @Test
    void moveLeftEatingHeadUpdate() {
        Board board = new Board("");
        board.setCell(5,6, Cell.BLANK);
        board.setCell(5,4, Cell.FOOD);
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.UP);
        snakeMovement.moveSnake(Direction.LEFT);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,5));
        assertEquals(Cell.SNAKE, board.getCell(5,4));
        assertEquals(2, snake.getCoordSnake().size());
    }

    @Test
    void moveRightNoEatingHeadUpdate() {
        Board board = new Board("");
        board.setCell(5,6, Cell.BLANK);
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.RIGHT);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,6));
        assertEquals(Cell.BLANK, board.getCell(5,5));
        assertEquals(1, snake.getCoordSnake().size());
    }

    @Test
    void moveRightEatingHeadUpdate() {
        Board board = new Board("");
        board.setCell(5,6, Cell.BLANK);
        board.setCell(5,6, Cell.FOOD);
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        snakeMovement.moveSnake(Direction.RIGHT);
        Snake snake = snakeMovement.getSnake();
        assertEquals(Cell.SNAKE, board.getCell(5,5));
        assertEquals(Cell.SNAKE, board.getCell(5,6));
        assertEquals(2, snake.getCoordSnake().size());
    }



    @Test
    @Disabled
    void foodIsEaten(){
        Board board = new Board();
        SnakeMovement snakeMovement = new SnakeMovement(board);

        Coordinate initialFoodPosition = board.getCoordinateFood(); // Initial food position
        ArrayList<Coordinate> initialSnakeBody = new ArrayList<>(snakeMovement.getSnake().getCoordSnake()); // Snake's initial body
        int initialSnakeSize = initialSnakeBody.size();

        // Move snake head to the food position
        snakeMovement.getSnake().setCoordSnake(new ArrayList<>(Arrays.asList(initialFoodPosition)));
        snakeMovement.eatFood(initialFoodPosition);

        // Get the snake's body after eating
        int updatedSnakeBody = snakeMovement.getSnake().getCoordSnake().size();

        // Verify the snake has grown
        assertEquals(initialSnakeSize + 1, updatedSnakeBody);

    }
}
