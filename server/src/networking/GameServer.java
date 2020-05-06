package networking;


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
import networking.packets.Packet020CreateGame;
import networking.packets.Packet021RequestGames;
import networking.packets.Packet022JoinGame;
import networking.packets.Packet023RequestGame;
import networking.packets.Packet024RemoveGameWithId;
import networking.packets.Packet025Score;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class GameServer {


    private Server server;
    private ServerListener serverListener;
    private int numberOfConnections;
    private int totalNumberOfConnections;
    private Map<String, Battlefield> gameMaps;
    private Map<String, Integer> playerCounts;
    private Map<String, Map<Integer, Double[]>> botLocations;

    //Server ports
    private static final int TCP_PORT = 5201;
    private static final int UDP_PORT = 5200;

    public void setBotLocations(String id, Map<Integer, Double[]> locations) {
        botLocations.put(id, locations);
    }

    public Map<String, Map<Integer, Double[]>> getBotLocations() {
        return this.botLocations;
    }

    public Map<String, Battlefield> getGameMaps() {
        return gameMaps;
    }

    public Map<String, Integer> getPlayerCounts() {
        return playerCounts;
    }

    public void removeGameInstances(String id) {
        botLocations.remove(id);
        playerCounts.remove(id);
        gameMaps.remove(id);
    }


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
        this.gameMaps = new HashMap<>();
        this.playerCounts = new HashMap<>();
        this.botLocations = new HashMap<>();
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

    public void createGame(String id, Battlefield map, int playerCount) {
        gameMaps.put(id, map);
        playerCounts.put(id, playerCount);

    }

    public void clearData() {
        this.gameMaps = new HashMap<>();
        this.playerCounts = new HashMap<>();
        this.botLocations = new HashMap<>();
        this.server = new Server();
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
        kryo.register(Packet020CreateGame.class);
        kryo.register(Packet021RequestGames.class);
        kryo.register(Packet022JoinGame.class);
        kryo.register(Packet023RequestGame.class);
        kryo.register(Packet024RemoveGameWithId.class);
        kryo.register(Packet025Score.class);
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

