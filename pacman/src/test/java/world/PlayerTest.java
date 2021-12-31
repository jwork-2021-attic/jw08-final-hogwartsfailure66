package world;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {
    public Player player;

    @Before
    public void setPlayer() {
        World world = new World();
        Map map = new Map();
        player = new Player(50, map, world, 1000);
    }

    @Test
    public void getLife() {
        assertEquals(3, player.getLife());
    }

    @Test
    public void setLife() {
        player.setLife(1);
        assertEquals(1, player.life);
    }

    @Test
    public void addLife() {
        player.addLife();
        assertEquals(4, player.life);
    }

    @Test
    public void minusLife() {
        player.minusLife();
        assertEquals(2, player.life);
    }

    @Test
    public void addScore() {
        player.addScore();
        assertEquals(1010, player.score);
    }

    @Test
    public void getScore() {
        assertEquals(1000, player.getScore());
    }
}