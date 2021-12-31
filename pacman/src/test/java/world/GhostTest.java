package world;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GhostTest {
    public Ghost ghost;

    @Before
    public void setGhost() {
        Map map = new Map();
        World world = new World();

        ghost = new Ghost(1, 50, map, world, 10, 5, Tile.FLOOR, 1);
    }

    @Test
    public void getOldTile() {
        assertEquals(Tile.FLOOR, ghost.getOldTile());
    }

    @Test
    public void setOldTile() {
        ghost.setOldTile(Tile.HEART);
        assertEquals(Tile.HEART, ghost.oldTile);
    }

    @Test
    public void getStartX() {
        assertEquals(10, ghost.getStartX());
    }

    @Test
    public void getStartY() {
        assertEquals(5, ghost.getStartY());
    }
}