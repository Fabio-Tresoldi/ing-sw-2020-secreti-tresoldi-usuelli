package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class PanTest {
    /* Power:
     *   You also win if your Worker moves down two or more levels
     */

    @Test
    void testPan() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        WinConditionPower power1;

        deck.fetchCard(God.PAN);
        player1.setCard(deck.popRandomCard());
        power1 = (WinConditionPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        worker1Player1.setLevel(Level.MIDDLE);

        //move
        board.move(player1, empty);
        //win condition power
        //assertTrue(power1.usePower(board));
    }
}
