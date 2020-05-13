package networking.packets;


import networking.Battlefield;

public class Packet005SendPlayerPosition extends Packet {
    public char pColor;
    public double xPosition;
    public double yPosition;
    public Battlefield battlefield;
    public String id;
    public int lives;
    public int connectionId;
    public boolean initial = false;

}
