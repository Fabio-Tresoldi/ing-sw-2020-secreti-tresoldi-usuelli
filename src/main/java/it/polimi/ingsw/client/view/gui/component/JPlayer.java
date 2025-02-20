package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the player in the GUI.
 * <p>
 * It contains its nickname and id, the card he owns, his workers (a male and a female) and information on this player
 * to be the current one in the game (which means he has to make some actions depending on the current state).
 */
public class JPlayer extends JButton implements ActionListener {

    private final String nickname;
    private final int id;

    private JCard card;
    private JWorker femaleWorker;
    private JWorker maleWorker;
    private boolean currentWorker;
    private boolean chooseWorker;

    private final String tagPath;
    private static final String activePath = "/img/labels/chosen_player.png";
    private boolean active;

    private final JLabel text;

    public static final int SIZE_X = 400;
    public static final int SIZE_Y = 100;
    public static final int FONT_SIZE = 35;

    public static final int CARD_SIZE_X = 150;
    public static final int CARD_SIZE_Y = 35;
    public static final int CARD_FONT_SIZE = 15;

    private int xSize;
    private int ySize;
    private int fontSize;


    /**
     * Constructor of the player, which is initialised with its nickname and index
     *
     * @param nickname the nickname of the player
     * @param index    index of the player
     */
    public JPlayer(String nickname, int index) {
        this.nickname = nickname;
        id = index;
        chooseWorker = false;
        active = false;
        xSize = SIZE_X;
        ySize = SIZE_Y;
        fontSize = FONT_SIZE;

        setPreferredSize(new Dimension(xSize, ySize));
        tagPath = "/img/workers/worker_" + (id + 1) + "/tag.png";
        ImageIcon icon = new ImageIcon(this.getClass().getResource(tagPath));
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(img));
        setOpaque(false);
        setLayout(new GridBagLayout());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setName("player");

        text = new JLabel(this.nickname);
        text.setForeground(Color.WHITE);
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        text.setOpaque(false);
        add(text, new GridBagConstraints());

        femaleWorker = null;
        maleWorker = null;
    }

    public String getNickname() {
        return nickname;
    }

    public void setJCard(JCard card) {
        this.card = card;
    }

    public JCard getJCard() {
        return card;
    }

    public void setWorkers(JWorker female, JWorker male) {
        femaleWorker = female;
        maleWorker = male;

        femaleWorker.getLocation().addActionListener(this);
        maleWorker.getLocation().addActionListener(this);
    }

    /**
     * Method that removes both workers for this player
     */
    public void removeWorkers() {
        ((JBlockDecorator) maleWorker.getLocation()).removeWorker();
        maleWorker = null;

        ((JBlockDecorator) femaleWorker.getLocation()).removeWorker();
        femaleWorker = null;
    }

    /**
     * Method that sets this player's worker in the given cell
     *
     * @param position the cell where the worker is placed
     */
    public void setUpWorker(JCell position) {
        if (femaleWorker == null)
            setUpFemaleWorker(position);
        else if (maleWorker == null)
            setUpMaleWorker(position);
    }

    public JWorker getFemaleWorker() {
        return femaleWorker;
    }

    public JWorker getMaleWorker() {
        return maleWorker;
    }

    public void setUpFemaleWorker(JCell position) {
        femaleWorker = new JWorker(JCellStatus.getWorkerType(id, true), position);
        femaleWorker.setId(1);
    }

    public void setUpMaleWorker(JCell position) {
        maleWorker = new JWorker(JCellStatus.getWorkerType(id, false), position);
        maleWorker.setId(2);
    }

    /**
     * Method that allows a player to pick one of his workers at the beginning of his turn
     */
    public void chooseWorker() {
        ((JBlockDecorator) this.maleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        ((JBlockDecorator) this.femaleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        chooseWorker = true;
    }

    public JWorker getCurrentWorker() {
        return (currentWorker) ? maleWorker : femaleWorker;
    }

    public void setCurrentWorker(boolean currentWorker) {
        this.currentWorker = currentWorker;
    }

    /**
     * Method that sets this player to be the current one in the game (which means he has to do some actions depending
     * on the current state)
     */
    public void active() {
        this.active = true;

        JLabel activeLabel = new JLabel();
        ImageIcon icon = new ImageIcon(this.getClass().getResource(activePath));
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        activeLabel.setIcon(icon);
        activeLabel.setLayout(new GridBagLayout());
        activeLabel.add(text, new GridBagConstraints());
        add(activeLabel, new GridBagConstraints());

        validate();
        repaint();
    }

    /**
     * Method that sets this player to be not the active anymore
     */
    public void disactive() {
        active = false;

        removeAll();
        add(text, new GridBagConstraints());

        validate();
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    public void setCardViewSize(boolean view) {
        if (view) {
            this.xSize = CARD_SIZE_X;
            this.ySize = CARD_SIZE_Y;
            this.fontSize = CARD_FONT_SIZE;
        } else {
            this.xSize = SIZE_X;
            this.ySize = SIZE_Y;
            this.fontSize = FONT_SIZE;
        }

        setPreferredSize(new Dimension(xSize, ySize));
        ImageIcon icon = new ImageIcon(this.getClass().getResource(tagPath));
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(img));
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));

        if (this.isActive()) {
            this.disactive();
            this.active();
        }

        revalidate();
    }

    public List<JWorker> getWorkers() {
        List<JWorker> workers = new ArrayList<>();

        if (femaleWorker != null)
            workers.add(femaleWorker);

        if (maleWorker != null)
            workers.add(maleWorker);

        return workers;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (chooseWorker) {
            currentWorker = ((JBlockDecorator) e.getSource()).getWorker().equals(maleWorker.getPawn().getDecoration());
            ((JBlockDecorator) maleWorker.getLocation()).removeDecoration();
            ((JBlockDecorator) femaleWorker.getLocation()).removeDecoration();
            chooseWorker = false;
        }
    }

    /**
     * Method that removes this player's workers from the board and unlinks them. It also sets this player to non-active
     * and sets his card to null
     */
    void clean() {
        if (femaleWorker != null && femaleWorker.getLocation() != null)
            ((JBlockDecorator) femaleWorker.getLocation()).removeWorker();

        if (maleWorker != null && maleWorker.getLocation() != null)
            ((JBlockDecorator) maleWorker.getLocation()).removeWorker();

        femaleWorker = null;
        maleWorker = null;

        chooseWorker = false;
        active = false;
        card = null;
    }
}
