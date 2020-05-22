package networking;

import Game.Menu;
import Game.maps.Battlefield;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
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
import networking.packets.Packet026FlagCaptured;
import networking.packets.Packet027FlagPickedUp;

import java.io.IOException;

public class ServerClient {
    private Client client;
    private ClientNetworkListener clientNetworkListener;
    private Menu menu;
    private String id;

    // Server ip address
    // Virtual server at 193.40.255.35
    private static final String SERVER_IP = "193.40.255.35";

    //Server ports
    private static final int TCP_PORT = 5201;
    private static final int UDP_PORT = 5200;


    /**
     * Creates the client and connects it to a server on localhost.
     * Initializes client listener
     */
    public ServerClient(Menu menu) {

        this.menu = menu;
        this.client = new Client();
        clientNetworkListener = new ClientNetworkListener();

        //Add and configure listener for the client
        setupListener();

        //Register packets for the client
        registerPackets();

        //Connect the client with a new thread
        new Thread(client).start();
        try {
            client.connect(9999, SERVER_IP, TCP_PORT, UDP_PORT);

        } catch (IOException e) {
            System.out.println(String.format("Unable to connect to the server at %s:%d", SERVER_IP, TCP_PORT));
            System.out.println("Starting in offline mode...");

        }
    }

    /**
     * @return menu assigned to this Server client.
     */
    public Menu getMenu() {
        return this.menu;
    }

    /**
     * Set menu.
     * Usually called from the menu class when exiting screen and making a new menu.
     *
     * @param menu menu to assign to this Server client.
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }


    /**
     * Set up client listener.
     */
    private void setupListener() {
        clientNetworkListener.init(this);
        this.client.addListener(clientNetworkListener);
    }

    /**
     * Get the client associated with this server client.
     *
     * @return Client instance to return
     */
    public Client getClient() {
        return this.client;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }


    /**
     * Register packets for client listener.
     */
    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(clientNetworkListener.getClass());
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
        kryo.register(Packet026FlagCaptured.class);
        kryo.register(Packet027FlagPickedUp.class);
        kryo.register(java.util.Map.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(Double[].class);
        kryo.register(Integer.class);
        kryo.register(Battlefield.class);

    }
}
