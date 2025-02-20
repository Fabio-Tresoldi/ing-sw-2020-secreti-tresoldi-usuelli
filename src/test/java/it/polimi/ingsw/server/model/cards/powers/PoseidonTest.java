package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PoseidonTest {
    /* Power:
     *   If your unmoved Worker is on the ground level, it may build up to three times
     */

    private Card poseidonCard() {
        Card poseidonCard = new Card();
        BuildPower poseidonPower = new BuildPower<>();
        Constraints constraints = poseidonPower.getConstraints();

        poseidonPower.setEffect(Effect.BUILD); //what
        poseidonPower.setWorkerType(WorkerType.UNMOVED_WORKER); //who
        poseidonPower.setWorkerInitPos(WorkerPosition.GROUND); //where
        poseidonPower.setTiming(Timing.END_TURN); //when
        constraints.setSameCell(false); //why
        constraints.setNotSameCell(false); //why
        constraints.setPerimCell(false); //why
        constraints.setNotPerimCell(false); //why
        constraints.setUnderItself(false); //why
        constraints.setNumberOfAdditional(3); //why
        poseidonPower.setAllowedAction(BlockType.DEFAULT); //how

        poseidonCard.addPower(poseidonPower);
        poseidonCard.setName("Poseidon");
        poseidonCard.setDescription("If your unmoved Worker is on the ground level, it may build up to three times");
        poseidonCard.setNumPlayer(3);
        poseidonPower.setPersonalMalus(null);

        return poseidonCard;
    }

    @Test
    void testUnmovedGroundLevelWorkerThreeBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(poseidonCard());
        power = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(3, 3);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 0);
        Block emptyPower2 = (Block) board.getCell(0, 1);
        Block emptyPower3 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power and an unmoved ground-level worker
        assertTrue(power.usePower(player1, emptyPower, board.getAround(emptyPower)));
        assertTrue(power.usePower(player1, emptyPower2, board.getAround(emptyPower2)));
        assertTrue(power.usePower(player1, emptyPower3, board.getAround(emptyPower3)));


        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower2.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower2.getLevel());
        assertEquals(Level.GROUND, emptyPower3.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower3.getLevel());
        assertEquals(emptyPower3, player1.getWorkers().get(1).getPreviousBuild());
        assertEquals(WorkerPosition.parseString("GROUND"), power.workerInitPos);
    }

    @Test
    void testNoCellToBuildUp() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(poseidonCard());
        power = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(3, 3);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 0);
        Block emptyPower2 = (Block) board.getCell(0, 1);
        Block emptyPower3 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        emptyPower.setLevel(Level.DOME);
        emptyPower.setPreviousLevel(Level.TOP);
        emptyPower2.setLevel(Level.DOME);
        emptyPower2.setPreviousLevel(Level.TOP);
        emptyPower3.setLevel(Level.DOME);
        emptyPower3.setPreviousLevel(Level.TOP);

        //build with power and an unmoved ground-level worker
        assertFalse(power.usePower(player1, emptyPower, board.getAround(emptyPower)));
        assertFalse(power.usePower(player1, emptyPower2, board.getAround(emptyPower2)));
        assertFalse(power.usePower(player1, emptyPower3, board.getAround(emptyPower3)));


        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(Level.TOP, emptyPower.getPreviousLevel());
        assertEquals(Level.DOME, emptyPower.getLevel());
        assertEquals(Level.TOP, emptyPower2.getPreviousLevel());
        assertEquals(Level.DOME, emptyPower2.getLevel());
        assertEquals(Level.TOP, emptyPower3.getPreviousLevel());
        assertEquals(Level.DOME, emptyPower3.getLevel());
        assertNull(player1.getWorkers().get(1).getPreviousBuild());
    }

    @Test
    void testOccupiedCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(poseidonCard());
        power = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(2, 2);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power and an unmoved ground-level worker
        assertFalse(power.usePower(player1, worker1Player1, board.getAround(worker1Player1)));


        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(Level.GROUND, worker1Player1.getPreviousLevel());
        assertEquals(Level.GROUND, worker1Player1.getLevel());
        assertNull(player1.getWorkers().get(1).getPreviousBuild());
    }

    @Test
    void testNotAdjacentCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(poseidonCard());
        power = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(3, 3);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block notAdjacentCell = (Block) board.getCell(4, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));


        //build with power and an unmoved ground-level worker
        assertFalse(power.usePower(player1, notAdjacentCell, board.getAround(notAdjacentCell)));


        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(Level.GROUND, notAdjacentCell.getPreviousLevel());
        assertEquals(Level.GROUND, notAdjacentCell.getLevel());
        assertNull(player1.getWorkers().get(1).getPreviousBuild());
    }
}
