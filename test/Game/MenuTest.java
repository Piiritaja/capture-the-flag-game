package Game;

import Game.maps.Battlefield;
import networking.packets.Packet020CreateGame;
import networking.packets.Packet021RequestGames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.*;

class MenuTest extends ApplicationTest {

    private Menu menu;

    @BeforeEach
    void setUp() {
        this.menu = new Menu();

    }

    @Test
    void getScreen() {
        assertNotNull(menu.getScreen());
    }


    @Test
    void changeNumberOfConnectionsText() {
        Menu menu2 = new Menu();
        try {
            Thread.sleep(500);

        } catch (Exception ignore) {

        }
        assertTrue(menu.getUsersOnlineText().getText().contains("Users online:"));

    }

    @Test
    void setNumberOfCurrentConnections() {
        menu.setNumberOfCurrentConnections(5);
        assertEquals(5, menu.getNumberOfConnections());
    }


    @Test
    void searchGameWithId() {
        Packet020CreateGame createGame = new Packet020CreateGame();
        menu.getScreen().setPlayerColor(1);
        createGame.gameId = "2346";
        createGame.battlefield = Battlefield.MAP1;
        createGame.playerCount = 4;
        menu.getServerClient().getClient().sendTCP(createGame);
        try {
            Thread.sleep(500);

        } catch (Exception ignore) {

        }
        menu.prepGame("2346");
        try {
            Thread.sleep(500);

        } catch (Exception ignore) {

        }
        assertEquals("2346", menu.getScreen().getGameId());
    }


    @Test
    void getMapImage() {
        assertNotNull(menu.getMapImage(0));
        assertNotNull(menu.getMapImage(1));

    }

    @Test
    void loadMaps() throws InterruptedException {
        menu.startScreen();
        Packet020CreateGame createGame = new Packet020CreateGame();
        menu.getScreen().setPlayerColor(1);
        createGame.gameId = "2346";
        createGame.battlefield = Battlefield.MAP1;
        createGame.playerCount = 4;
        menu.getServerClient().getClient().sendTCP(createGame);
        try {
            Thread.sleep(500);

        } catch (Exception ignore) {

        }

        try{
            Packet021RequestGames requestGames = new Packet021RequestGames();
            menu.getServerClient().getClient().sendTCP(requestGames);
            Thread.sleep(1000);
        } catch (Exception e){
            fail();
        }


    }



    @Test
    void loadMapImages() {
        assertEquals(2,menu.loadMapImages().size());
    }

}