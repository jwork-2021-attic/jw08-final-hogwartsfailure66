package world;

import data.CommonData;
import data.Direction;
import data.State;
//import screen.LoseScreen;
import screen.OneScreen;
//import screen.PlayScreen;
//import screen.WinScreen;
import server.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class World {
    public Tile[][] tiles;
    public int width;
    public int height;
    public int winScore;
    public boolean winFlag;
    public boolean loseFlag;
    //    public Lock lock;
    public Player player;
    public List<Ghost> ghosts;
    public Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    public OneScreen mainScreen;
    public List<Player> players;
    public boolean isOnline = false;
    public Server server = null;
    public int id;
    public String process = "";

    public World() {
        width = CommonData.WORLD_WIDTH;
        height = CommonData.WORLD_HEIGHT;
        winFlag = false;
        loseFlag = false;
//        lock = new ReentrantLock();
    }

    public void setMainScreen(OneScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void setWinScore(int beanCount) {
        winScore = CommonData.BEAN_VALUE * beanCount;
    }

    public int getWinScore() {
        return winScore;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Tile getTileAt(int x, int y) {
        return tiles[x][y];
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process += process;
    }

    public void writeProcess() throws IOException {
        File file = new File(CommonData.PROCESS_FILE);
        file.createNewFile();
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(process.getBytes());
        stream.flush();
        stream.close();
    }

    public List<Direction> getPassableDirections(int x, int y) {
        List<Direction> list = new ArrayList<Direction>();
        for (Direction d : directions) {
            Tile tile = tiles[x + d.getDx()][y + d.getDy()];
            if (tile.isPassable()) {
                list.add(d);
            }
        }
        return list;
    }

    public void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setServer(Server server) {
        this.server = server;
        isOnline = true;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void movePlayer(int x, int y, Direction direction, Object object) throws IOException {
//        System.out.println("move player");
        tiles[x][y] = Tile.FLOOR;
        object.setX(x + direction.getDx());
        object.setY(y + direction.getDy());
        tiles[x + direction.getDx()][y + direction.getDy()] = Tile.PLAYER;
        object.setDirection(Direction.STAY);
        if (isOnline) {
            String s = "move player " + object.id + " " + x + " " + y;
            s += directionToString(direction);
            server.send(s);
        }
    }

    public String directionToString(Direction direction) {
        switch (direction) {
            case UP:
                return " up";
            case DOWN:
                return " down";
            case LEFT:
                return " left";
            case RIGHT:
                return " right";
        }
        return "";
    }

    public void moveGhost(int x, int y, Direction direction, Ghost ghost) throws IOException {
//        System.out.println("ghost:" + ghost.id + " pos:" + x + " " + y + " direction:" + direction + " oldtile:" + ghost.oldTile);
        if (ghost.oldTile == Tile.PLAYER || ghost.oldTile == Tile.GHOST) {
            System.out.println("GHOST OLD TILE ERROR");
            System.exit(1);
        }
        if (tiles[x + direction.getDx()][y + direction.getDy()] != Tile.GHOST) {
            tiles[x][y] = ghost.oldTile;
            ghost.setX(x + direction.getDx());
            ghost.setY(y + direction.getDy());
            if (tiles[x + direction.getDx()][y + direction.getDy()] == Tile.PLAYER) {
                ghost.oldTile = Tile.FLOOR;
            } else {
                ghost.oldTile = tiles[x + direction.getDx()][y + direction.getDy()];
            }

            tiles[x + direction.getDx()][y + direction.getDy()] = Tile.GHOST;
        }
        if (isOnline) {
            String s = "move ghost " + ghost.id + " " + x + " " + y;
            s += directionToString(direction);
            server.send(s);
        }
    }

    public void resetPlayer() {
        if (!isOnline && mainScreen.state != State.REPLAY_ONLINE) {
            tiles[player.getX()][player.getY()] = Tile.FLOOR;
            player.setDirection(Direction.STAY);
            player.setX(CommonData.PLAYER_START_X);
            player.setY(CommonData.PLAYER_START_Y);
            tiles[CommonData.PLAYER_START_X][CommonData.PLAYER_START_Y] = Tile.PLAYER;
        } else {
            resetOnlinePlayer();
        }
    }

    public void resetGhosts() {
        System.out.println("reset ghosts");
        for (Ghost ghost : ghosts) {
            if (ghost.oldTile == Tile.PLAYER) {
                tiles[ghost.getX()][ghost.getY()] = Tile.FLOOR;
            } else {
                tiles[ghost.getX()][ghost.getY()] = ghost.oldTile;
            }
            ghost.oldTile = Tile.FLOOR;
            ghost.setDirection(Direction.STAY);
            int startX = ghost.getStartX();
            int startY = ghost.getStartY();
            ghost.setX(startX);
            ghost.setY(startY);
            //tiles[startX][startY] = Tile.GHOST;
        }
    }

    public void resetPlayerGhosts() {
        resetPlayer();
        resetGhosts();
    }

    public void resetOnlinePlayer() {
        Player player0 = players.get(0);
        Player player1 = players.get(1);
        tiles[player0.getX()][player0.getY()] = Tile.FLOOR;
        tiles[player1.getX()][player1.getY()] = Tile.FLOOR;
        player0.setX(CommonData.PLAYER1_START_X);
        player0.setY(CommonData.PLAYER_START_Y);
        player0.setDirection(Direction.STAY);
        player1.setX(CommonData.PLAYER2_START_X);
        player1.setY(CommonData.PLAYER_START_Y);
        player1.setDirection(Direction.STAY);
        tiles[CommonData.PLAYER1_START_X][CommonData.PLAYER_START_Y] = Tile.PLAYER;
        tiles[CommonData.PLAYER2_START_X][CommonData.PLAYER_START_Y] = Tile.PLAYER;
    }

    public void onlineHit(int x, int y) throws InterruptedException {
        tiles[x][y] = Tile.FLOOR;
        TimeUnit.MILLISECONDS.sleep(200);
        resetOnlinePlayer();
        resetGhosts();
        mainScreen.mainFrame.repaint();
        TimeUnit.SECONDS.sleep(1);
    }

    public void playerHitGhost(int x, int y) throws InterruptedException, IOException {
        Player player;
        if (!isOnline && mainScreen.state != State.REPLAY_ONLINE) {
            player = this.player;
        } else {
            if (players.get(0).getX() == x && players.get(0).getY() == y) {
                player = players.get(0);
            } else {
                player = players.get(1);
            }
        }
        if (player.minusLife()) {
            // RESET PLAYER AND GHOSTS;
            if (isOnline) {
                server.send("hit " + player.id + " " + x + " " + y);
            }
            tiles[x][y] = Tile.FLOOR;
            TimeUnit.MILLISECONDS.sleep(200);
            resetPlayerGhosts();
            System.out.println("LIFE-1, WAIT 1s");
            mainScreen.mainFrame.repaint();
            TimeUnit.SECONDS.sleep(1);
        } else {
            // LOSE
            tiles[x][y] = Tile.FLOOR;
            loseFlag = true;
            System.out.println("SOMEONE LOSE");
            mainScreen.mainFrame.repaint();
            if (mainScreen.state != State.REPLAY && mainScreen.state != State.REPLAY_ONLINE) writeProcess();
            if (isOnline) {
                if (players.get(0).getX() == x && players.get(0).getY() == y) {
                    server.send("win");
                    TimeUnit.MILLISECONDS.sleep(100);
                    mainScreen.state = State.SINGLE_LOSE;
                } else {
                    server.send("lose");
                    TimeUnit.MILLISECONDS.sleep(100);
                    mainScreen.state = State.SINGLE_WIN;
                }
            } else {
                TimeUnit.MILLISECONDS.sleep(100);
                mainScreen.state = State.SINGLE_LOSE;
            }

//            mainScreen.mainFrame.setScreen(new LoseScreen(mainScreen.mainFrame));
//            mainScreen.mainFrame.repaint();
        }
    }

    public synchronized void setTile(int x, int y, Direction direction, Tile nextTile, Player player, Ghost ghost) throws InterruptedException, IOException {
//        System.out.println("set tile");
        if (player != null) {
            if (mainScreen.state != State.REPLAY && mainScreen.state != State.REPLAY_ONLINE) {
                process += "move player " + player.id + " " + x + " " + y;
                process += directionToString(direction);
                process += "\n";
            }
//            System.out.println("get player, next tile:" + nextTile);
            switch (nextTile) {
                case GHOST:
                    System.out.println("player hit ghost, player " + player.id + " at " + player.getX() + " " + player.getY());
                    playerHitGhost(x, y);
                    break;
                case BEAN:
//                    System.out.println("bean");
                    player.addScore();
                    movePlayer(x, y, direction, player);
                    if (isOnline) server.send("score " + player.id + " " + player.getScore());
                    if (!isOnline && player.getScore() >= winScore) {
                        winFlag = true;
                        System.out.println("WIN");
                        mainScreen.mainFrame.repaint();
                        if (mainScreen.state != State.REPLAY && mainScreen.state != State.REPLAY_ONLINE) writeProcess();
                        TimeUnit.MILLISECONDS.sleep(100);
                        mainScreen.state = State.SINGLE_WIN;
//                        mainScreen.mainFrame.setScreen(new WinScreen(mainScreen.mainFrame));
//                        mainScreen.mainFrame.repaint();
                    }
                    if (isOnline) {
                        if (players.get(0).score + players.get(1).score >= winScore) {
                            winFlag = true;
                            System.out.println("someone win");
                            mainScreen.mainFrame.repaint();
                            if (mainScreen.state != State.REPLAY && mainScreen.state != State.REPLAY_ONLINE)
                                writeProcess();
                            TimeUnit.MILLISECONDS.sleep(100);
                            if (players.get(0).getScore() > players.get(1).getScore()) {
                                mainScreen.state = State.SINGLE_WIN;
                                server.send("lose");
                            } else {
                                mainScreen.state = State.SINGLE_LOSE;
                                server.send("win");
                            }
                        }
                    }
                    break;
                case HEART:
                    player.addLife();

                    if (isOnline && player.id == 1) {
                        server.send("life");
                    }
                    movePlayer(x, y, direction, player);
                    break;
                case FLOOR:
                    movePlayer(x, y, direction, player);
                    break;
            }
        } else if (ghost != null) {
            if (mainScreen.state != State.REPLAY && mainScreen.state != State.REPLAY_ONLINE) {
                process += "move ghost " + ghost.id + " " + x + " " + y;
                process += directionToString(direction);
                process += "\n";
//                switch (direction) {
//                    case UP:
//                        process += " up";
//                        break;
//                    case DOWN:
//                        process += " down";
//                        break;
//                    case LEFT:
//                        process += " left";
//                        break;
//                    case RIGHT:
//                        process += " right";
//                        break;
//                }
            }
            switch (nextTile) {
                case PLAYER:
                    System.out.println("ghost hit player");
                    moveGhost(x, y, direction, ghost);
                    playerHitGhost(x + direction.getDx(), y + direction.getDy());
                    break;
                case GHOST:
                    // do not move
                    break;
                case FLOOR:
                case BEAN:
                case HEART:
                    moveGhost(x, y, direction, ghost);
                    break;
            }
        }
    }
}
