package networking.packets;

import Game.maps.Battlefield;

public class Packet019UpdateScore extends Packet {
    public Battlefield map;
    public String team;
    public int score;
}
