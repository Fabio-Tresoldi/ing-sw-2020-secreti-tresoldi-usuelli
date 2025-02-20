package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.panels.GamePanel;
import it.polimi.ingsw.client.view.gui.panels.ManagerPanel;
import it.polimi.ingsw.communication.message.header.DemandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the Map in the GUI.
 * <p>
 * It contains its dimension, a matrix of cells, information about the current worker and the current player.
 * It also has a {@code gamePanel} and {@code gamePanelButton}
 */
public class JMap extends JPanel implements ActionListener {

    public static final int DIM = 5;
    private final JCell[][] cellButton;
    private final List<JCell> activeCells;
    private final List<JCell> powerCells;
    private JWorker currentWorker;
    private JPlayer currentPlayer;
    private JCellStatus turn;
    private JCellStatus power;
    private int positioning;
    private JButton gamePanelButton;
    private GamePanel gamePanel;
    private ManagerPanel managerPanel;

    /**
     * Constructor of the map, setting all of its attributes to default
     */
    public JMap() {
        super(new GridBagLayout());

        setOpaque(false);
        setVisible(true);

        positioning = -1;
        power = JCellStatus.NONE;
        turn = JCellStatus.NONE;

        activeCells = new ArrayList<>();
        powerCells = new ArrayList<>();
        cellButton = new JCell[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = i;
                c.gridy = DIM - j - 1;
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1;
                c.weightx = 1;
                cellButton[i][j] = new JBlockDecorator(new JBlock(i, j));
                cellButton[i][j].addActionListener(this);
                add(cellButton[i][j], c);
            }
        }
    }

    public void setCurrentPlayer(JPlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public JPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentWorker(JWorker worker) {
        currentWorker = worker;
    }

    public JWorker getCurrentWorker() {
        return currentWorker;
    }

    public void setAround(List<JCell> where, JCellStatus how) {
        if (how.equals(JCellStatus.MOVE) || how.equals(JCellStatus.BUILD)) {
            activeCells.clear();
            for (JCell cell : where) {
                activeCells.add(cell);
                ((JBlockDecorator) cell).addDecoration(how);
            }
        } else if (how.equals(JCellStatus.USE_POWER)) {
            powerCells.clear();
            powerCells.addAll(where);
        } else if (how.equals(JCellStatus.MALUS)) {
            for (JCell cell : where) {
                activeCells.add(cell);
                ((JBlockDecorator) cell).addDecoration(how);
            }
        }

        repaint();
        validate();
    }

    public void setPossibleMove(List<JCell> where) {
        turn = JCellStatus.MOVE;
        setAround(where, JCellStatus.MOVE);
    }

    public void setPossibleBuild(List<JCell> where) {
        turn = JCellStatus.BUILD;
        setAround(where, JCellStatus.BUILD);
    }

    public void setPossibleUsePowerMove(List<JCell> where) {
        power = JCellStatus.MOVE;
        setAround(where, JCellStatus.USE_POWER);
    }

    public void setPossibleUsePowerBuild(List<JCell> where) {
        power = JCellStatus.BUILD;
        setAround(where, JCellStatus.USE_POWER);
    }

    public void setPossibleMalus(List<JCell> where) {
        setAround(where, JCellStatus.MALUS);
    }

    /**
     * Method that moves the current worker to the given cell
     *
     * @param where the cell where the worker is moved
     */
    public void moveWorker(JCell where) {
        moveWorker(currentWorker, where);
    }

    /**
     * Method that moves the given worker to the chosen cell
     *
     * @param worker the worker that is moved
     * @param where  the cell where the worker is moved to
     */
    public void moveWorker(JWorker worker, JCell where) {
        worker.setLocation(where);
    }

    /**
     * Method that allows the current worker to switch its position with the worker in the chosen cell
     *
     * @param where the cell where the worker wants to move
     */
    public void switchWorkers(JCell where) {
        switchWorkers(currentWorker, where);
    }

    /**
     * Method that allows the given worker to switch its position with the worker in the chosen cell
     *
     * @param currWorker the worker that uses switch
     * @param nextCell   the cell where the opponent worker (that is switched) was located
     */
    public void switchWorkers(JWorker currWorker, JCell nextCell) {
        if (((JBlockDecorator) nextCell).isFree())
            moveWorker(currWorker, nextCell);
        else {
            JWorker enemyWorker = ((JBlockDecorator) nextCell).getJWorker();
            JCell prevCell = currWorker.getLocation();

            ((JBlockDecorator) nextCell).removeWorker();
            ((JBlockDecorator) prevCell).removeWorker();

            currWorker.setCell(nextCell);
            enemyWorker.setCell(prevCell);
        }
    }

    /**
     * Method that shows the cells where the player can use his God power. It also removes eventual cells where a malus
     * is active
     */
    public void showPowerCells() {
        for (JCell cell : activeCells)
            if (!((JBlockDecorator) cell).getDecoration().equals(JCellStatus.MALUS))
                ((JBlockDecorator) cell).removeDecoration();

        for (JCell cell : powerCells)
            ((JBlockDecorator) cell).addDecoration(JCellStatus.USE_POWER);

        repaint();
        validate();
    }

    /**
     * Method that hides the cells where the player may have used the power (or because he doesn't want to)
     */
    public void hidePowerCells() {
        for (JCell cell : powerCells)
            ((JBlockDecorator) cell).removeDecoration();

        for (JCell cell : activeCells)
            if (((JBlockDecorator) cell).getDecoration() == null)
                ((JBlockDecorator) cell).addDecoration(turn);

        repaint();
        validate();
    }

    /**
     * Method that removes the given decoration from the map
     *
     * @param jCellStatus the decoration that is removed
     */
    public void removeDecoration(JCellStatus jCellStatus) {
        Arrays.stream(cellButton)
                .flatMap(Arrays::stream)
                .map(jCell -> ((JBlockDecorator) jCell))
                .filter(c -> c.getDecoration() != null)
                .filter(c -> c.getDecoration().equals(jCellStatus))
                .forEach(JBlockDecorator::removeDecoration);
    }

    public JCell getCell(int x, int y) {
        return cellButton[x][y];
    }

    public boolean isPowerActive() {
        return !power.equals(JCellStatus.NONE);
    }

    public void workersPositioning() {
        if (positioning == -1)
            positioning = 2;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setManagerPanel(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
    }

    public void powerButtonManager(JButton btn) {
        gamePanelButton = btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCell src = (JCell) e.getSource();
        if (src.getName().equals("cell")) {
            if (activeCells.contains(src) || powerCells.contains(src)) {
                JCellStatus status = ((JBlockDecorator) src).getDecoration();
                if (status == null) return;

                if (!status.equals(JCellStatus.NONE) && !status.equals(JCellStatus.MALUS)) {
                    for (JCell cell : activeCells)
                        ((JBlockDecorator) cell).clean();
                    activeCells.clear();

                    for (JCell cell : powerCells)
                        ((JBlockDecorator) cell).clean();
                    powerCells.clear();

                    if (status.equals(JCellStatus.BUILD))
                        ((JBlockDecorator) src).buildUp();
                    else if (status.equals(JCellStatus.MOVE))
                        moveWorker(src);
                    else if (status.equals(JCellStatus.USE_POWER)) {
                        if (power.equals(JCellStatus.BUILD)) {
                            if (managerPanel.getGui().getClientModel().getPlayer().getCard().isDomePower())
                                ((JBlockDecorator) src).addDecoration(JCellStatus.DOME);
                            else
                                ((JBlockDecorator) src).buildUp();
                        } else if (power.equals(JCellStatus.MOVE)) {
                            if (managerPanel.getGui().getClientModel().getPlayer().getCard().isPushPower())
                                pushWorker(src);
                            else
                                switchWorkers(src);
                        }
                        power = JCellStatus.NONE;
                    }

                    gamePanelButton.setEnabled(false);
                    gamePanel.generateDemand(((JBlockDecorator) src).getBlock(), status);

                    validate();
                    repaint();
                }
            } else if (positioning > 0 && ((JBlockDecorator) src).isFree()) {
                currentPlayer.setUpWorker(src);
                positioning--;

                if (positioning == 0)
                    gamePanel.generateDemand(currentPlayer.getWorkers().stream().map(JWorker::getLocation).collect(Collectors.toList()), JCellStatus.NONE);

                revalidate();
            } else if (!((JBlockDecorator) src).isFree() && getCurrentPlayer().getWorkers().contains(((JBlockDecorator) src).getJWorker()) && managerPanel.getGui().getClientModel().getCurrentState().equals(DemandType.CHOOSE_WORKER)) {
                gamePanel.generateDemand(((JBlockDecorator) src).getBlock(), JCellStatus.CHOOSE_WORKER);
                currentWorker = ((JBlockDecorator) src).getJWorker();
            }
        }
    }

    /**
     * Method that allows the given worker to push an opponent's worker that is located on the chosen cell
     *
     * @param worker the worker to make the push with
     * @param where  the cell where the opponent's worker is placed
     */
    public void pushWorker(JWorker worker, JCell where) {
        JWorker jWorkerToPush = ((JBlockDecorator) where).getJWorker();

        if (jWorkerToPush != null) {
            JCell newLocationWorkerToPush = lineEqTwoPoints(worker.getLocation(), where);
            switchWorkers(jWorkerToPush, newLocationWorkerToPush);
        }
        switchWorkers(worker, where);
    }

    /**
     * Method that allows the current worker to push an opponent's worker that is located on the chosen cell
     *
     * @param where the cell where the opponent's worker is placed
     */
    public void pushWorker(JCell where) {
        pushWorker(currentWorker, where);
    }

    /**
     * Method that locates the cell where the worker is pushed after an opponent used Minotaur power
     *
     * @param from the cell where the opponent's worker comes from
     * @param to   the cell where the opponent's worker moved to
     * @return the cell where the worker is pushed to
     */
    private JCell lineEqTwoPoints(JCell from, JCell to) {
        if (from == null) return null;
        if (to == null) return null;
        if (to.getXCoordinate() == from.getXCoordinate() && to.getYCoordinate() == from.getYCoordinate())
            return null; //from and to cannot be the same cell!

        if (to.getXCoordinate() != from.getXCoordinate()) { //y = mx + q (slope-intercept)
            float m = ((float) (to.getYCoordinate() - from.getYCoordinate())) / ((float) (to.getXCoordinate() - from.getXCoordinate())); //slope
            float q = from.getYCoordinate() - m * from.getXCoordinate(); //intercept

            return fetchNextCell(from, to, m, q);
        } else { //x = k (vertical line)
            int y;
            if (from.getYCoordinate() < to.getYCoordinate())
                y = to.getYCoordinate() + 1;
            else
                y = to.getYCoordinate() - 1;

            if (y >= 0 && y <= 4)
                return getCell(to.getXCoordinate(), y);
            else
                return null;
        }
    }

    /**
     * Method that fetches the next cell and is used for Minotaur power that pushes opponent's worker to the next cell
     *
     * @param from the cell where the worker (that is using Minotaur's power) comes from
     * @param to   the cell where the worker (that is using Minotaur's power) moves to
     * @param m    the slope
     * @param q    the intercept
     * @return the new cell where the worker is pushed
     */
    private JCell fetchNextCell(JCell from, JCell to, float m, float q) {

        int newX;

        if (from.getXCoordinate() < to.getXCoordinate())
            newX = to.getXCoordinate() + 1;
        else
            newX = to.getXCoordinate() - 1;

        return getCell(newX, (int) (m * newX + q));
    }

    /**
     * Method that cleans the map, by setting all of his attributes back to default and removing the game panel.
     */
    public void clean() {
        Arrays.stream(cellButton)
                .flatMap(Arrays::stream)
                .forEach(JCell::clear);

        activeCells.clear();
        powerCells.clear();
        currentWorker = null;
        currentPlayer = null;
        positioning = -1;
        power = JCellStatus.NONE;
        turn = JCellStatus.NONE;
        gamePanel.removeAll();
        gamePanel = null;

        validate();
        repaint();
    }
}
