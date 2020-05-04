package networking.packets;

import Game.maps.Battlefield;

import java.util.Map;

public class Packet021RequestGames extends Packet {
    public Map<String, Battlefield> maps;
    public Map<String, Integer> playerCounts;
}
