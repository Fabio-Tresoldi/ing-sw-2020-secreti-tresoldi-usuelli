package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WaitingRoomPanel extends SantoriniPanel {

    private static final String imgPath = "menu.png";

    public WaitingRoomPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon("img/buttons/waiting.png");
        Image img = icon.getImage().getScaledInstance( 800, 400, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        JLabel stand = new JLabel(icon);
        stand.setOpaque(false);

        add(stand, new GridBagConstraints());
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getClientModel().getAnswer().getHeader().equals(AnswerType.CHANGE_TURN)) {
            mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());
            gui.free();
            return;
        }

        if (gui.getClientModel().getCurrentState().equals(DemandType.CREATE_GAME)) {
            mg.addPanel(new NumPlayerPanel(panelIndex, panels));
            this.panelIndex.next(this.panels);
            return;
        }

        if (mg.getGame().getJDeck().getList().isEmpty()) {
            List<ReducedCard> reducedCardList = gui.getClientModel().getDeck();
            List<God> godList = reducedCardList.stream()
                    .map(ReducedCard::getGod)
                    .collect(Collectors.toList());

            int i = 0;
            JDeck deck = new JDeck(godList);
            for (JGod god : deck.getList())
                god.setDescription(reducedCardList.get(i++).getDescription());

            mg.getGame().setJDeck(deck);
        }

        if (gui.getClientModel().getCurrentState() != null && gui.getClientModel().getCurrentState().equals(DemandType.START))
            WaitingRoomPanel.setUpJPlayers((ManagerPanel) panels);

        if (gui.getClientModel().getAnswer().getContext() != null) {
            switch (gui.getAnswer().getContext()) {
                case GOD:
                    mg.addPanel(new ChooseCardsPanel(panelIndex, panels, mg.getGame().getJDeck()));
                    ((ChooseCardsPanel) mg.getCurrentPanel()).numPlayer = gui.getClientModel().getNumberOfPlayers();
                    this.panelIndex.next(this.panels);
                    break;

                case CARD:
                    if (!gui.getClientModel().getCurrentPlayer().isCreator()) {
                        mg.addPanel(new ChooseGodPanel(panelIndex, panels, mg.getGame().getJDeck()));
                        ((ChooseGodPanel) mg.getCurrentPanel()).enableChoose(gui.getClientModel().isYourTurn());
                        mg.getCurrentPanel().updateFromModel();
                        this.panelIndex.next(this.panels);
                    }
                    break;

                default:
                    break;
            }
        }

        gui.free();
    }

    static void setUpJPlayers(ManagerPanel mg) {
        GUI gui = mg.getGui();

        List<ReducedPlayer> playerList = gui.getClientModel().getOpponents();
        playerList.add(gui.getClientModel().getPlayer());

        if (mg.getGame().getNumPlayer() > 0) return;

        for (ReducedPlayer p : playerList) {
            mg.getGame().addPlayer(p.getNickname(), WaitingRoomPanel.toColorIndex(p.getColor()));

            if (p.getNickname().equals(gui.getClientModel().getCurrentPlayer().getNickname()))
                mg.getGame().setCurrentPlayer(mg.getGame().getPlayer(p.getNickname()));

            if (p.getNickname().equals(gui.getClientModel().getPlayer().getNickname()))
                mg.setClientPlayer(mg.getGame().getPlayer(p.getNickname()));
        }
    }

    private static int toColorIndex (String color) {
        switch (color) {
            case "cyan":
                return 0;

            case "red":
                return 1;

            case "yellow":
                return 2;

            default:
                return -1;
        }
    }
}