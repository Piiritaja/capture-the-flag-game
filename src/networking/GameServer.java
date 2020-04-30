package networking;


import Game.maps.Battlefield;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryo.Kryo;
import networking.packets.Packet000RequestAccess;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;
import networking.packets.Packet004RequestPlayers;
import networking.packets.Packet005SendPlayerPosition;
import networking.packets.Packet006RequestBotsLocation;
import networking.packets.Packet007SendBotsLocation;
import networking.packets.Packet008SendPlayerID;
import networking.packets.Packet009BotHit;
import networking.packets.Packet010PlayerMovement;
import networking.packets.Packet011PlayerMovementStop;
import networking.packets.Packet012UpdatePlayerPosition;
import networking.packets.Packet013PlayerHit;
import networking.packets.Packet014PlayerDisconnected;
import networking.packets.Packet015RequestAI;
import networking.packets.Packet016SendAiPlayer;
import networking.packets.Packet017GamePlayerShoot;
import networking.packets.Packet018PlayerConnected;
import networking.packets.Packet019UpdateScore;

import java.io.IOException;


public class GameServer {


    private Server server;
    private ServerListener serverListener;
    private int numberOfConnections;
    private int totalNumberOfConnections;

    //Server ports
    private static final int TCP_PORT = 5201;
    private static final int UDP_PORT = 5200;


    /**
     * Save the number of connected clients to a variable.
     *
     * @param numberOfConnections amount to set the connections to.
     */
    public void setNumberOfConnections(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    public int getTotalNumberOfConnections() {
        return this.totalNumberOfConnections;
    }

    public void setTotalNumberOfConnections(int c) {
        this.totalNumberOfConnections = c;

    }

    /**
     * Get the number of connected clients to the server.
     *
     * @return number of connected clients.
     */
    public int getNumberOfConnections() {
        return this.numberOfConnections;
    }

    /**
     * Set up server and initializes server listener.
     */
    public GameServer() {
        this.server = new Server();
        this.serverListener = new ServerListener(this.server, this);
        setUpServer();

    }


    /**
     * Start server, add listener and register packets.
     */
    public void setUpServer() {
        server.addListener(serverListener);
        registerPackets();

        try {
            server.start();
            server.bind(TCP_PORT, UDP_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Register packets for server listener.
     */
    public void registerPackets() {
        Kryo kryo = server.getKryo();
        kryo.register(serverListener.getClass());
        kryo.register(Packet000RequestAccess.class);
        kryo.register(Packet001AllowAccess.class);
        kryo.register(Packet002RequestConnections.class);
        kryo.register(Packet003SendConnections.class);
        kryo.register(Packet004RequestPlayers.class);
        kryo.register(Packet005SendPlayerPosition.class);
        kryo.register(Packet006RequestBotsLocation.class);
        kryo.register(Packet007SendBotsLocation.class);
        kryo.register(Packet008SendPlayerID.class);
        kryo.register(Packet009BotHit.class);
        kryo.register(Packet010PlayerMovement.class);
        kryo.register(Packet011PlayerMovementStop.class);
        kryo.register(Packet012UpdatePlayerPosition.class);
        kryo.register(Packet013PlayerHit.class);
        kryo.register(Packet014PlayerDisconnected.class);
        kryo.register(Packet015RequestAI.class);
        kryo.register(Packet016SendAiPlayer.class);
        kryo.register(Packet017GamePlayerShoot.class);
        kryo.register(Packet018PlayerConnected.class);
        kryo.register(Packet019UpdateScore.class);
        kryo.register(java.util.Map.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(Double[].class);
        kryo.register(Integer.class);
        kryo.register(Battlefield.class);
    }

    public static void main(String[] args) {
        new GameServer();
        System.out.println(String.format("Server started at TCP/UDP (%d,%d)", TCP_PORT, UDP_PORT));

    }


}


