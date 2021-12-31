package world;

import data.CommonData;
import data.Direction;
import data.State;
import org.junit.Before;
import org.junit.Test;
import screen.OneScreen;
import server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorldTest {
    public World world;
    public Map map;

    @Before
    public void setWorld() throws IOException {
        world = new World();
        world.winScore = 2000;
        world.process = "lalala";
        map = new Map();
        map.parseMap("board.txt");
        world.player = new Player(1000, map, world, 0);
        world.players = new ArrayList<>();
        world.players.add(new Player(1, new Map(), world, 0));
        world.players.add(new Player(1, new Map(), world, 0));
        world.tiles = map.gameMap;
        List<Ghost> ghosts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int startX = CommonData.GHOST_START_X[i];
            int startY = CommonData.GHOST_START_Y[i];
            Ghost ghost = new Ghost(1, 100, map, world, startX, startY, Tile.FLOOR, i);
            System.out.println(ghost.id);
            ghosts.add(ghost);
        }
        world.ghosts = ghosts;
        world.isOnline = false;
        world.mainScreen = new OneScreen(new ApplicationMain());
        world.mainScreen.state = State.SINGLE_PLAY;
    }

    @Test
    public void setWinScore() {
        world.setWinScore(100);
        assertEquals(1000, world.winScore);
    }

    @Test
    public void getWinScore() {
        assertEquals(2000, world.getWinScore());
    }

    @Test
    public void getTileAt() {
        world.tiles = new Tile[2][2];
        world.tiles[0][0] = Tile.FLOOR;
        world.tiles[0][1] = Tile.HEART;
        world.tiles[1][0] = Tile.BEAN;
        world.tiles[1][1] = Tile.FLOOR;
        assertEquals(Tile.BEAN, world.getTileAt(1, 0));
    }

    @Test
    public void setId() {
        world.setId(100);
        assertEquals(100, world.id);
    }

    @Test
    public void getProcess() {
        assertEquals("lalala", world.getProcess());
    }

    @Test
    public void setProcess() {
        world.setProcess("hahaha");
        assertEquals("lalalahahaha", world.process);
    }

    @Test
    public void setPlayer() {
        Player player = new Player(1000, new Map(), world, 50);
        world.setPlayer(player);
        assertEquals(player, world.player);
    }

    @Test
    public void resetPlayer() {
        world.player = new Player(1000, new Map(), world, 50);
        world.player.x = 0;
        world.player.y = 0;
        world.resetPlayer();
        assertEquals(11, world.player.x);
    }

    @Test
    public void resetGhosts() {
        world.ghosts.get(1).x = 0;
        world.resetGhosts();
        assertEquals(11, world.ghosts.get(1).x);
    }

    @Test
    public void playerHitGhost() throws IOException, InterruptedException {
        world.playerHitGhost(0, 0);
        assertEquals(2, world.player.life);
        world.player.life = 1;
        world.playerHitGhost(0, 0);
        assertEquals(State.SINGLE_LOSE, world.mainScreen.state);
    }

    @Test
    public void setMainScreen() {
        OneScreen mainScreen = new OneScreen(new ApplicationMain());
        world.setMainScreen(mainScreen);
        assertEquals(mainScreen, world.mainScreen);
    }

    @Test
    public void setTiles() {
        Tile[][] tiles = new Tile[2][2];
        tiles[0][0] = Tile.FLOOR;
        tiles[0][1] = Tile.HEART;
        tiles[1][0] = Tile.BEAN;
        tiles[1][1] = Tile.FLOOR;
        world.setTiles(tiles);
        assertEquals(tiles, world.tiles);
    }

    @Test
    public void setGhosts() {
        List<Ghost> ghosts = new ArrayList<>();
        ghosts.add(new Ghost(1, 1, map, world, 0, 0, Tile.HEART, 0));
        world.setGhosts(ghosts);
        assertEquals(ghosts, world.ghosts);
    }

    @Test
    public void setServer() {
        Server server = new Server(new OneScreen(new ApplicationMain()));
        world.setServer(server);
        assertEquals(server, world.server);
    }

    @Test
    public void movePlayer() throws IOException {
        Player player = new Player(1, new Map(), world, 0);
        world.movePlayer(0, 0, Direction.RIGHT, player);
        assertEquals(1, player.x);
        world.isOnline = true;
        try {
            world.movePlayer(0, 0, Direction.RIGHT, player);
            assertEquals(1, player.x);
        } catch (Exception e) {

        }
    }

    @Test
    public void directionToString() {
        assertEquals(" up", world.directionToString(Direction.UP));
        assertEquals(" down", world.directionToString(Direction.DOWN));
        assertEquals(" left", world.directionToString(Direction.LEFT));
        assertEquals(" right", world.directionToString(Direction.RIGHT));
    }

    @Test
    public void onlineHit() throws InterruptedException {
        world.onlineHit(0, 0);
        assertEquals(Tile.PLAYER, world.tiles[CommonData.PLAYER1_START_X][CommonData.PLAYER_START_Y]);
    }

    @Test
    public void writeProcess() throws IOException {
        world.process = "move ghost 0 1 9 right\n" +
                "move ghost 3 21 9 left\n" +
                "move ghost 1 11 9 up\n" +
                "move ghost 2 11 11 right\n" +
                "move ghost 3 20 9 right\n" +
                "move ghost 0 2 9 right\n" +
                "move ghost 2 12 11 right\n" +
                "move ghost 1 11 8 right\n" +
                "move player 2 11 15 left";
        world.writeProcess();
    }

    @Test
    public void moveGhost() throws IOException {
        world.tiles[2][1] = Tile.PLAYER;
        Ghost ghost = new Ghost(1, 1, new Map(), world, 1, 1, Tile.BEAN, 1);
        world.moveGhost(1, 1, Direction.RIGHT, ghost);
        assertEquals(Tile.FLOOR, ghost.oldTile);
        world.isOnline = true;
        try {
            Ghost ghost1 = new Ghost(1, 1, new Map(), world, 1, 1, Tile.BEAN, 1);
            world.moveGhost(1, 1, Direction.RIGHT, ghost1);
        } catch (Exception e) {

        }
    }

    @Test
    public void setTile() throws IOException, InterruptedException {
        world.winScore = 1740;
        world.player.score = 1730;
        world.player.x = 1;
        world.player.y = 1;
        world.setTile(1, 1, Direction.RIGHT, Tile.BEAN, world.player, null);
        assertEquals(State.SINGLE_WIN, world.mainScreen.state);
        world.player.score = 1730;
        world.player.x = 1;
        world.player.y = 1;
        world.setTile(1, 1, Direction.RIGHT, Tile.HEART, world.player, null);
        assertEquals(4, world.player.life);
    }
}