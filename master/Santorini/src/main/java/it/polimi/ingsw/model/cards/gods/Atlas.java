/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.gods.exceptions.OccupiedCellException;
import it.polimi.ingsw.model.cards.gods.exceptions.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;

import java.util.List;

/*Power:
 *  Your Worker may build a dome at any level
 */

public class Atlas extends Card {
    /*@class
     * it portrays the power of Atlas
     */

    public Atlas() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.ATLAS, Effect.BUILD);
    }

    @Override
    public void usePower(Cell cell) throws Exception/*OccupiedCellException*/ {
        /*@function
         * it implements Atlas' power
         */

        if (!cell.isFree()) throw new OccupiedCellException("Occupied cell!");

        cell.setLevel(Level.DOME);
    }

    @Override
    public void usePower(List<Player> opponents) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public boolean usePower() throws UnusedPowerException{
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }
}