package world;

import data.Direction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectTest {
    public Object object;
    public World world;

    @Before
    public void setObject() {
        object = new Object();
        object.x = 0;
        object.y = 1;
        world = new World();
        world.tiles = new Tile[2][2];
        world.tiles[0][0] = Tile.FLOOR;
        world.tiles[0][1] = Tile.HEART;
        world.tiles[1][0] = Tile.BEAN;
        world.tiles[1][1] = Tile.FLOOR;
        object.world = world;
    }

    @Test
    public void setDirection() {
        object.setDirection(Direction.RIGHT);
        assertEquals(object.direction, Direction.RIGHT);
    }


    @Test
    public void getNextTile() {
        Tile tile = object.getNextTile(0, 0, Direction.RIGHT);
        assertEquals(Tile.BEAN, tile);
    }

    @Test
    public void canMove() {
        Tile tile = Tile.WALL;
        assertFalse(object.canMove(tile));
    }

    @Test
    public void getX() {
        assertEquals(0, object.getX());
    }

    @Test
    public void getY() {
        assertEquals(1, object.getY());
    }

    @Test
    public void setX() {
        object.setX(7);
        assertEquals(7, object.x);
    }

    @Test
    public void setY() {
        object.setY(9);
        assertEquals(9, object.y);
    }
}