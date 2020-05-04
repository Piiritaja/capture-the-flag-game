package networking.packets;

import Game.maps.Battlefield;

public class Packet020CreateGame extends Packet {
    public Battlefield battlefield;
    public int playerCount;
}
