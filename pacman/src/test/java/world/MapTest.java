package world;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MapTest {
    public Map map;

    @Before
    public void setMap() {
        map = new Map();
    }

    @Test
    public void parseMap() throws IOException {
        Tile[][] tiles = map.parseMap("board.txt");
        assertEquals(Tile.PLAYER, tiles[11][15]);
    }

    @Test
    public void getBeanCount() throws IOException {
        Tile[][] tiles = map.parseMap("board_online.txt");
        assertEquals(173, map.getBeanCount());
    }
}