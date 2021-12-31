package screen;

import data.CommonData;
import data.Direction;
import data.State;
import org.junit.Before;
import org.junit.Test;
import server.Server;
import world.ApplicationMain;
import world.Map;
import world.Player;
import world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OneScreenTest {
    public OneScreen mainScreen;

    @Before
    public void setMainScreen() {
        mainScreen = new OneScreen(new ApplicationMain());
        List<Player> players = new ArrayList<Player>();
        World world = new World();
        Map map = new Map();
        Player player0 = new Player(500, map, world, 0);
        player0.id = 0;
        player0.setX(CommonData.PLAYER1_START_X);
        Player player1 = new Player(500, map, world, 0);
        player1.id = 1;
        player1.setX(CommonData.PLAYER2_START_X);
        players.add(player0);
        players.add(player1);
        mainScreen.players = players;
    }

    @Test
    public void setSinglePlayScreen() {
        mainScreen.setSinglePlayScreen();
        assertEquals(2, mainScreen.player.id);
    }

    @Test
    public void stringToDirection() {
        assertEquals(Direction.UP, mainScreen.stringToDirection("up"));
    }

    @Test
    public void setServer() {
        mainScreen.setServer();
        assertNotNull(mainScreen.server);
    }

    @Test
    public void setClient() {
        try {
            mainScreen.setClient();
            assertNotNull(mainScreen.client);
        } catch (Exception e) {

        }
    }

    @Test
    public void setOnlinePlayScreen() {
        mainScreen.setOnlinePlayScreen();
        assertEquals(1, mainScreen.players.get(1).id);
    }

    @Test
    public void setServerScreen() {
        try {
            mainScreen.setServerScreen();
            assertEquals(State.SERVER_PLAY, mainScreen.state);
        } catch (Exception e) {

        }

    }

    @Test
    public void handleClientListen() throws IOException, InterruptedException {
        mainScreen.handleClientListen("set");
        assertEquals(State.CLIENT_PLAY, mainScreen.state);
        mainScreen.handleClientListen("win");
        assertEquals(State.SINGLE_WIN, mainScreen.state);
        mainScreen.handleClientListen("lose");
        assertEquals(State.SINGLE_LOSE, mainScreen.state);
        mainScreen.handleClientListen("score 0 10");
        assertEquals(10, mainScreen.players.get(0).score);
    }

    @Test
    public void handleServerListen() {
        mainScreen.handleServerListen("move player 1 up");
        assertEquals(Direction.UP, mainScreen.players.get(1).direction);
    }
}