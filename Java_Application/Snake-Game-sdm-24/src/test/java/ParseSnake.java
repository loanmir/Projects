import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParseSnake {

    @Test
    @Disabled
    void snakeGrowsAfterEating() {
        Board board = new Board("");
        int[][] tmp = {{5,4},{5,3},{5,2},{4,2}};
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);
        ArrayList<Coordinate> coordBody = Coordinate.createCoordinateArray(tmp);
        snakeMovement.getSnake().setCoordSnake(coordBody);
        snakeMovement.moveSnake(Direction.RIGHT);
        ArrayList<Coordinate> newCoordBody = snakeMovement.getSnake().getCoordSnake();
        assertEquals(5, newCoordBody.size());
    }

    @Test
    @Disabled
    void snakeTailRowNotChangingAfterEating() {
        Board board = new Board("");
        int[][] tmp = {{5,4},{5,3},{5,2},{4,2}};
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);

        ArrayList<Coordinate> coordBody = Coordinate.createCoordinateArray(tmp);

        snakeMovement.getSnake().setCoordSnake(coordBody);
        Coordinate coordTail = new Coordinate(tmp[3]);

        snakeMovement.moveSnake(Direction.RIGHT);
        Coordinate newCoordTail = snakeMovement.getSnake().getCoordinateLastPieceOfBody();

        assertEquals(coordTail.getY(), newCoordTail.getY());
    }

    @Test
    @Disabled
    void snakeTailColNotChangingAfterEating() {
        Board board = new Board("");
        int[][] body = {{5,4},{5,3},{5,2},{4,2}};
        SnakeMovement snakeMovement = new SnakeMovement(board);
        snakeMovement.setCurrentDirection(Direction.RIGHT);

        ArrayList<Coordinate> coordBody = Coordinate.createCoordinateArray(body);

        snakeMovement.getSnake().setCoordSnake(coordBody);
        Coordinate coordTail = new Coordinate(body[3]);

        snakeMovement.moveSnake(Direction.RIGHT);
        Coordinate newCoordTail = snakeMovement.getSnake().getCoordinateLastPieceOfBody();

        assertEquals(coordTail.getX(), newCoordTail.getX());
    }
}
