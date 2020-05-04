package networking.packets;


import Game.maps.Battlefield;

import java.util.Map;

public class Packet007SendBotsLocation {
    public Map<Integer, Double[]> locations;
    public Battlefield battlefield;
}
