package server;

import data.CommonData;
import data.State;
import screen.OneScreen;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public OneScreen mainScreen;
    boolean haveClient;
    public int num;
    public Socket socket;

    public Server(OneScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.haveClient = false;
    }

    public void send(String s) throws IOException {
        System.out.println("server send: " + s);
        DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
        stream.writeUTF(s);
        stream.flush();
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
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(CommonData.SERVER_PORT);
            System.out.println("server start: " + serverSocket.getInetAddress() + " port: " + serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!haveClient) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();
                if (socket != null) {
                    this.socket = socket;
                    System.out.println("get client");
                    System.out.println("客户端:" + socket.getInetAddress().getLocalHost() + "已连接到服务器");
                    haveClient = true;
                    DataInputStream stream = new DataInputStream(socket.getInputStream());
                    new Thread(new ServerListen(mainScreen, socket, stream)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mainScreen.state = State.CONNECTED;
        try {
            mainScreen.setServerScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
