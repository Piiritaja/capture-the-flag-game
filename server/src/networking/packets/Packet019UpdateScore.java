package networking.packets;


import networking.Battlefield;

public class Packet019UpdateScore extends Packet {
    public Battlefield map;
    public String team;
    public int score;
}
