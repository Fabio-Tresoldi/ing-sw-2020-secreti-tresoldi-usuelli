package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JMap extends JPanel implements ActionListener {

    public final static int DIM = 5;
    private JCell[][] cellButton;
    private List<JCell> activeCells;
    private List<JCell> powerCells;
    private JWorker currentWorker;
    private JPlayer currentPlayer;
    private JCellStatus turn;
    private JCellStatus power;
    private int positioning;
    private JButton gamePanelButton;

    public JMap() {
        super(new GridBagLayout());

        setOpaque(false);
        setVisible(true);

        this.positioning = -1;
        this.power = JCellStatus.NONE;
        this.turn = JCellStatus.NONE;

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
        this.currentWorker = worker;
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

    public void moveWorker(JCell where) {
        currentWorker.setLocation(where);
    }

    public void showPowerCells() {
        for (JCell cell : activeCells)
            if (!((JBlockDecorator)cell).getDecoration().equals(JCellStatus.MALUS))
                ((JBlockDecorator)cell).removeDecoration();

        for (JCell cell : powerCells)
            ((JBlockDecorator)cell).addDecoration(JCellStatus.USE_POWER);

        repaint();
        validate();
    }

    public void hidePowerCells() {
        for (JCell cell : powerCells)
            ((JBlockDecorator)cell).removeDecoration();

        for (JCell cell : activeCells)
            if (((JBlockDecorator)cell).getDecoration() == null)
                ((JBlockDecorator)cell).addDecoration(turn);

        repaint();
        validate();
    }

    public JCell getCell(int x, int y) {
        return cellButton[x][y];
    }

    public boolean isPowerActive() {
        return !power.equals(JCellStatus.NONE);
    }

    public void workersPositioning() {
        this.positioning = 2;
    }

    public void powerButtonManager(JButton btn) {
        this.gamePanelButton = btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCell src = (JCell) e.getSource();
        if (src.getName().equals("cell")) {
            if (activeCells.contains(src) || powerCells.contains(src)) {
                JCellStatus status = ((JBlockDecorator) src).getDecoration();
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
                        if (power.equals(JCellStatus.BUILD))
                            ((JBlockDecorator) src).buildUp();
                        else if (power.equals(JCellStatus.MOVE))
                            moveWorker(src);
                        power = JCellStatus.NONE;
                    }

                    /******* mi disabilita il bottone nel game panel *******************/
                    this.gamePanelButton.setEnabled(false);
                    /********************************************************************/
                    validate();
                    repaint();
                }
            } else if (this.positioning > 0 && ((JBlockDecorator)src).isFree()) {
                currentPlayer.setUpWorker(src);
                this.positioning--;
                revalidate();
            }
        }
    }
}
