package networking.packets;


import networking.Battlefield;

public class Packet016SendAiPlayer extends Packet {
    public char pColor;
    public double xPosition;
    public double yPosition;
    public Battlefield battlefield;
    public String id;
    public int connectionId;
}