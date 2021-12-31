package world;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class TileTest {

    @Test
    public void glyph() {
        assertEquals((char) 250, Tile.BEAN.glyph());
    }

    @Test
    public void color() {
        assertEquals(Color.black, Tile.FLOOR.color());
    }
}