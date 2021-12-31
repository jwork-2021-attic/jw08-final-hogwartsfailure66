package world;

import data.Direction;
import screen.OneScreen;

import java.io.IOException;

public class Replay implements Runnable {
    public OneScreen mainScreen;
    public String process;
    public String[] command;
    public int len;
    public int cnt = 0;

    public Replay(OneScreen mainScreen, String process) {
        this.mainScreen = mainScreen;
        this.process = process;
        command = process.split("\n");
        len = command.length;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (true) {
            if (cnt >= len) break;
            String c[] = command[cnt].split(" ");
            if (c[1].equals("player")) {
                Player player = null;
                int x = Integer.parseInt(c[3]), y = Integer.parseInt(c[4]);
                Direction direction = mainScreen.stringToDirection(c[5]);
                if (c[2].equals("2")) {
//                    mainScreen.player.setDirection(mainScreen.stringToDirection(c[5]));
                    player = mainScreen.player;
                } else {
//                    mainScreen.players.get(Integer.parseInt(c[2])).setDirection(mainScreen.stringToDirection(c[5]));
                    player = mainScreen.players.get(Integer.parseInt(c[2]));
                }
                Tile nextTile = player.getNextTile(x, y, direction);
                try {
                    mainScreen.world.setTile(x, y, direction, nextTile, player, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // ghost
                Ghost ghost = mainScreen.ghosts.get(Integer.parseInt(c[2]));
                int x = Integer.parseInt(c[3]), y = Integer.parseInt(c[4]);
                Direction direction = mainScreen.stringToDirection(c[5]);
                Tile nextTile = ghost.getNextTile(x, y, direction);
                try {
                    mainScreen.world.setTile(x, y, direction, nextTile, null, ghost);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cnt++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
