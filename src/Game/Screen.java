package Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;


public class Screen extends JPanel implements ActionListener, KeyListener {

    Timer t = new Timer(10, this);
    Player p = new Player(10,10,10,10,0, 0);

    public Screen() {
        addKeyListener(this);
        setFocusable(true);

        t.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        p.tick();

        repaint();
    }

    public void paint(Graphics g) {
        g.clearRect(0,0,getWidth(), getHeight());

        p.draw(g);
    }

    public void keyTyped(KeyEvent keyEvent) {

    }

    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getKeyCode()) {
            case KeyEvent.VK_D:
                p.setDx(1);
                break;
            case KeyEvent.VK_S:
                p.setDy(1);
                break;
            case KeyEvent.VK_A:
                p.setDx(-1);
                break;
            case KeyEvent.VK_W:
                p.setDy(-1);
                break;
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        switch(keyEvent.getKeyCode()) {
            case KeyEvent.VK_D:
            case KeyEvent.VK_A:
                p.setDx(0);
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_W:
                p.setDy(0);
                break;
        }
    }

    public static void main(String[] args) {

    }
}
