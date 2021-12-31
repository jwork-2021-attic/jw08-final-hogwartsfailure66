package world;

import org.junit.Before;
import org.junit.Test;
import screen.OneScreen;

import static org.junit.Assert.*;

public class ReplayTest {
    public Replay replay;

    @Before
    public void setReplay() {
        OneScreen mainScreen = new OneScreen(new ApplicationMain());
        mainScreen.setSinglePlayScreen();
        replay = new Replay(mainScreen, "move ghost 0 1 9 right\nmove player 2 11 15 left");
    }

    @Test
    public void run() {
        replay.run();
        assertEquals(2, replay.cnt);
    }
}