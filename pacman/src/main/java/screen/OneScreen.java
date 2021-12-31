package screen;

import asciiPanel.AsciiPanel;
import data.CommonData;
import data.Direction;
import data.State;
import server.Client;
import server.Server;
import world.*;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OneScreen implements Screen {
    public ApplicationMain mainFrame;
    public State state;
    public World world;
    public Map map;
    public Player player;
    public ExecutorService executorService;
    public List<Ghost> ghosts;
    public List<Player> players;
    public Server server = null;
    public Client client = null;

    public OneScreen(ApplicationMain mainFrame) {
        this.mainFrame = mainFrame;
        state = State.START;
    }

    public void setSinglePlayScreen() {
        world = new World();
        map = new Map();
        player = new Player(500, map, world, 0);
        player.id = 2;
        ghosts = new ArrayList<Ghost>();

        if (world.tiles == null) {
            try {
                Tile[][] tmp = map.parseMap(CommonData.SINGLE_PLAYER_MAP);
                world.setTiles(tmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 4; i++) {
            int startX = CommonData.GHOST_START_X[i];
            int startY = CommonData.GHOST_START_Y[i];
            Ghost ghost = new Ghost(1, 100, map, world, startX, startY, Tile.FLOOR, i);
            System.out.println(ghost.id);
            ghosts.add(ghost);
        }

        world.setWinScore(map.getBeanCount());
        if (state != State.REPLAY) world.setProcess("map " + CommonData.SINGLE_PLAYER_MAP + "\n");
        System.out.println(world.getWinScore());
        world.setMainScreen(this);
        world.setPlayer(player);
        world.setGhosts(ghosts);
        if (state != State.REPLAY && state != State.REPLAY_ONLINE) singlePlay();
//        singlePlay();
    }

    public void singlePlay() {
        executorService = Executors.newCachedThreadPool();
        executorService.execute(new Thread(player));
        System.out.println("ghost num " + ghosts.size());
        executorService.execute(new Thread(ghosts.get(0)));
        executorService.execute(new Thread(ghosts.get(1)));
        executorService.execute(new Thread(ghosts.get(2)));
        executorService.execute(new Thread(ghosts.get(3)));
    }

    public void setReplayScreen() throws IOException, InterruptedException {
        File file = new File(CommonData.PROCESS_FILE);
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String s = null;
        s = bufferedReader.readLine();
        String c[] = s.split(" ");
        if (c[0].equals("map")) {
            if (c[1].equals(CommonData.SINGLE_PLAYER_MAP)) {
                setSinglePlayScreen();
            } else {
                state = State.REPLAY_ONLINE;
                setOnlinePlayScreen();
            }
        }
        String process = "";
        while ((s = bufferedReader.readLine()) != null) {
            process += s;
            process += "\n";
        }
        System.out.println(process);
        new Thread(new Replay(this, process)).start();
    }

    public Direction stringToDirection(String c) {
        Direction direction = Direction.STAY;
        switch (c) {
            case "up":
                direction = Direction.UP;
                break;
            case "down":
                direction = Direction.DOWN;
                break;
            case "left":
                direction = Direction.LEFT;
                break;
            case "right":
                direction = Direction.RIGHT;
                break;
        }
        return direction;
    }

    public void setServer() {
        server = new Server(this);
        new Thread(server).start();
    }

    public void setClient() throws IOException {
        client = new Client(this);
        client.connect();
    }

    public void setOnlinePlayScreen() {
        world = new World();
        map = new Map();
        players = new ArrayList<Player>();
        Player player0 = new Player(500, map, world, 0);
        player0.id = 0;
        player0.setX(CommonData.PLAYER1_START_X);
        Player player1 = new Player(500, map, world, 0);
        player1.id = 1;
        player1.setX(CommonData.PLAYER2_START_X);
        players.add(player0);
        players.add(player1);
        ghosts = new ArrayList<Ghost>();

        if (world.tiles == null) {
            try {
                Tile[][] tmp = map.parseMap(CommonData.ONLINE_MAP);
                world.setTiles(tmp);
//                for (Tile[] tileLine : tmp) {
//                    for (Tile tile : tileLine) {
//                        System.out.printf(tile + "\t");
//                    }
//                    System.out.println();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 4; i++) {
            int startX = CommonData.GHOST_START_X[i];
            int startY = CommonData.GHOST_START_Y[i];
            Ghost ghost = new Ghost(1, 100, map, world, startX, startY, Tile.FLOOR, i);
            System.out.println(ghost.id);
            ghosts.add(ghost);
        }
        world.setWinScore(map.getBeanCount());
        System.out.println(world.getWinScore());
        world.setMainScreen(this);
        world.setPlayers(players);
        world.setGhosts(ghosts);
        if (state != State.REPLAY) world.setProcess("map " + CommonData.ONLINE_MAP + "\n");
    }

    public void onlinePlay() {
        executorService = Executors.newCachedThreadPool();
        executorService.execute(new Thread(players.get(0)));
        executorService.execute(new Thread(players.get(1)));
        executorService.execute(new Thread(ghosts.get(0)));
        executorService.execute(new Thread(ghosts.get(1)));
        executorService.execute(new Thread(ghosts.get(2)));
        executorService.execute(new Thread(ghosts.get(3)));
    }

    public void setServerScreen() throws IOException {
        setOnlinePlayScreen();
        world.setServer(server);
        world.setId(0);
        world.player = players.get(0);
        System.out.println("server world");
        state = State.SERVER_PLAY;
        server.send("set");
//        System.out.println("server world online:" + world.isOnline);
        onlinePlay();
//        server.send("move player 0 10 15 left");
    }

    public void handleClientListen(String s) throws IOException, InterruptedException {
        System.out.println(s);
        if (s.equals("set")) {
            setOnlinePlayScreen();
            world.setId(1);
            world.player = players.get(1);
            System.out.println("client world");
            state = State.CLIENT_PLAY;
        } else if (s.equals("life")) {
            players.get(1).addLife();
        } else {
            String c[] = s.split(" ");
            if (c[0].equals("move")) {
                if (c[1].equals("player")) {
                    Direction direction = stringToDirection(c[5]);
                    world.movePlayer(Integer.parseInt(c[3]), Integer.parseInt(c[4]), direction,
                            players.get(Integer.parseInt(c[2])));
                } else if (c[1].equals("ghost")) {
                    Direction direction = stringToDirection(c[5]);
                    world.moveGhost(Integer.parseInt(c[3]), Integer.parseInt(c[4]), direction,
                            ghosts.get(Integer.parseInt(c[2])));
                }
            } else if (c[0].equals("score")) {
                players.get(Integer.parseInt(c[1])).score = Integer.parseInt(c[2]);
            } else if (c[0].equals("win")) {
                world.winFlag = true;
                state = State.SINGLE_WIN;
            } else if (c[0].equals("lose")) {
                world.loseFlag = true;
                state = State.SINGLE_LOSE;
            } else if (c[0].equals("hit")) {
                if (c[1].equals("1")) {
                    players.get(1).minusLife();
                }
                world.onlineHit(Integer.parseInt(c[2]), Integer.parseInt(c[3]));
            }
        }
    }

    public void handleServerListen(String s) {
        System.out.println(s);
        String c[] = s.split(" ");
        if (c[0].equals("move")) {
            if (c[1].equals("player") && c[2].equals("1")) {
                Direction direction = Direction.STAY;
                switch (c[3]) {
                    case "up":
                        direction = Direction.UP;
                        break;
                    case "down":
                        direction = Direction.DOWN;
                        break;
                    case "left":
                        direction = Direction.LEFT;
                        break;
                    case "right":
                        direction = Direction.RIGHT;
                        break;
                }
                players.get(1).setDirection(direction);
            }
        } else {
            System.out.println("server listen || client send error!!!");
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        int life;
        Player currentPlayer;
        Player rival;
        switch (state) {
            case START:
                terminal.write("PACMAN", 0, 0);
                terminal.write("Press ENTER to START", 0, 1);
                terminal.write("Press S to be SERVER", 0, 2);
                terminal.write("Press C to be CLIENT", 0, 3);
                break;
            case SINGLE_PLAY:
            case REPLAY:
                terminal.write("Score:" + player.getScore(), 0, 0);
                terminal.write("Life:", 12, 0);
                life = player.getLife();
                for (int i = 0; i < life; i++) {
                    terminal.write(Tile.HEART.glyph(), 17 + i, 0, Tile.HEART.color());
                }
                for (int i = 0; i < CommonData.WORLD_WIDTH; i++) {
                    for (int j = 0; j < CommonData.WORLD_HEIGHT; j++) {
                        Tile tile = world.getTileAt(i, j);
                        terminal.write(tile.glyph(), i, j + CommonData.DISPLAY_Y_OFFSET, tile.color());
                    }
                }
                break;
            case SERVER_PLAY:
            case REPLAY_ONLINE:
                currentPlayer = players.get(0);
                rival = players.get(1);
                terminal.write("Score:" + currentPlayer.getScore(), 0, 0);
                terminal.write("Life:", 12, 0);
                terminal.write("RIVAL Score:" + rival.getScore(), 0, 1);
                life = currentPlayer.getLife();
                for (int i = 0; i < life; i++) {
                    terminal.write(Tile.HEART.glyph(), 17 + i, 0, Tile.HEART.color());
                }
                for (int i = 0; i < CommonData.WORLD_WIDTH; i++) {
                    for (int j = 0; j < CommonData.WORLD_HEIGHT; j++) {
                        Tile tile = world.getTileAt(i, j);
                        terminal.write(tile.glyph(), i, j + CommonData.DISPLAY_Y_OFFSET, tile.color());
                    }
                }
                break;
            case CLIENT_PLAY:
                currentPlayer = players.get(1);
                rival = players.get(0);
                terminal.write("Score:" + currentPlayer.getScore(), 0, 0);
                terminal.write("Life:", 12, 0);
                terminal.write("RIVAL Score:" + rival.getScore(), 0, 1);
                life = currentPlayer.getLife();
                for (int i = 0; i < life; i++) {
                    terminal.write(Tile.HEART.glyph(), 17 + i, 0, Tile.HEART.color());
                }
                for (int i = 0; i < CommonData.WORLD_WIDTH; i++) {
                    for (int j = 0; j < CommonData.WORLD_HEIGHT; j++) {
                        Tile tile = world.getTileAt(i, j);
                        terminal.write(tile.glyph(), i, j + CommonData.DISPLAY_Y_OFFSET, tile.color());
                    }
                }
                break;
            case SINGLE_WIN:
                terminal.write("YOU WIN!!!", 0, 0);
                terminal.write("PRESS ENTER TO RESTART", 0, 1);
                terminal.write("Press S to be SERVER", 0, 2);
                terminal.write("Press C to be CLIENT", 0, 3);
                terminal.write("Press R to be REPLAY", 0, 4);
                break;
            case SINGLE_LOSE:
                terminal.write("YOU LOSE...", 0, 0);
                terminal.write("PRESS ENTER TO RESTART", 0, 1);
                terminal.write("Press S to be SERVER", 0, 2);
                terminal.write("Press C to be CLIENT", 0, 3);
                terminal.write("Press R to be REPLAY", 0, 4);
                break;
            case WAITING_FOR_CLIENT:
                terminal.write("WAITING...", 0, 0);
                break;
            case CONNECTED:
                terminal.write("CONNECTED!!!", 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) throws IOException, InterruptedException {
        Player currentPlayer;
        switch (state) {
            case START:
                switch (key.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        setSinglePlayScreen();
                        state = State.SINGLE_PLAY;
                        break;
                    case KeyEvent.VK_S:
                        setServer();
                        state = State.WAITING_FOR_CLIENT;
                        break;
                    case KeyEvent.VK_C:
                        setClient();
                        state = State.CONNECTED;
                        break;
//                    case KeyEvent.VK_R:
//                        state = State.REPLAY;
//                        setReplayScreen();
//                        break;
                }
                break;
            case SINGLE_WIN:
            case SINGLE_LOSE:
                switch (key.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        setSinglePlayScreen();
                        state = State.SINGLE_PLAY;
                        break;
                    case KeyEvent.VK_S:
                        setServer();
                        state = State.WAITING_FOR_CLIENT;
                        break;
                    case KeyEvent.VK_C:
                        setClient();
                        state = State.CONNECTED;
                        break;
                    case KeyEvent.VK_R:
                        state = State.REPLAY;
                        setReplayScreen();
                        break;
                }
                break;
            case SINGLE_PLAY:
                switch (key.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        player.setDirection(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        player.setDirection(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        player.setDirection(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        player.setDirection(Direction.RIGHT);
                        break;
                }
                break;
            case SERVER_PLAY:
                currentPlayer = players.get(0);
                System.out.println("server play key pressed:" + key.getKeyCode());
                System.out.println("current player: " + currentPlayer.id);
                switch (key.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        currentPlayer.setDirection(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        currentPlayer.setDirection(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        currentPlayer.setDirection(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        currentPlayer.setDirection(Direction.RIGHT);
                        break;
                }
                break;
            case CLIENT_PLAY:
                String s = "move player 1";
                switch (key.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        s += " up";
                        client.send(s);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        s += " down";
                        client.send(s);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        s += " left";
                        client.send(s);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        s += " right";
                        client.send(s);
                        break;
                }
                break;
        }
        return this;
    }
}
