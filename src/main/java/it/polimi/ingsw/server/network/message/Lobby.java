package it.polimi.ingsw.server.network.message;

import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.network.ServerConnectionSocket;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lobby {
    private final String id;
    private final Game game;
    private final Controller controller;
    private final List<View> playerViewList;
    private String messagePath;
    public static final String backupPath = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_lobby.xml";
    private final Map<ServerClientHandler, View> playingConnection;
    private final Map<View, ReducedPlayer> playerColor;
    private static final Random randomLobby = new SecureRandom();
    private int numberOfPlayers;
    private boolean reloaded;
    public final Object lockLobby;
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());

    public Lobby() throws ParserConfigurationException, SAXException {
        this(new Game());
        reloaded = false;
    }

    public Lobby(Game game) {
        id = String.valueOf(randomLobby.nextInt());

        this.game = game;
        controller = new Controller(game);
        playerViewList = new ArrayList<>();
        playingConnection = new HashMap<>();
        playerColor = new HashMap<>();

        lockLobby = new Object();
        numberOfPlayers = -1;

        reloaded = true;

        File f = new File(backupPath);
        boolean b;
        try {
            if (f.exists())
                b = f.delete();
            b = f.createNewFile();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an exception", e);
        }
    }

    public String getMessagePath() {
        return this.messagePath;
    }

    public String getId() {
        return id;
    }

    public int getNumberOfPlayers() {
        int ret;

        synchronized (lockLobby) {
            ret = numberOfPlayers;
        }

        return ret;
    }

    public List<ReducedPlayer> getReducedPlayerList() {
        List<ReducedPlayer> reducedPlayerList = new ArrayList<>();
        ReducedPlayer reducedPlayer;

        synchronized (playerViewList) {
            synchronized (playerColor) {
                for (View v : playerViewList) {
                    reducedPlayer = new ReducedPlayer(v.getPlayer());
                    reducedPlayer.setColor(playerColor.get(v).getColor());
                    reducedPlayerList.add(reducedPlayer);
                }
            }
        }

        return reducedPlayerList;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        synchronized (lockLobby) {
            this.numberOfPlayers = numberOfPlayers;
        }
    }

    public void deletePlayer(ServerClientHandler player) {
        synchronized (playerViewList) {
            synchronized (playingConnection) {
                playerViewList.remove(playingConnection.get(player));
                playingConnection.remove(player);
            }
        }
    }

    public void setCurrentPlayer(String player) {
        synchronized (game) {
            game.setCurrentPlayer(game.getPlayer(player));
        }
    }

    public boolean isReloaded() {
        boolean ret;

        synchronized (lockLobby) {
            ret = reloaded;
        }

        return ret;
    }

    public Game getGame() {
        return game;
    }

    public synchronized Controller getController() {
        return controller;
    }

    public synchronized boolean addPlayer(String player, ServerClientHandler serverClientHandler) {
        if (playingConnection.size() == numberOfPlayers) return false;

        RemoteView v = new RemoteView(player, serverClientHandler);

        playerViewList.add(v);
        playingConnection.put(serverClientHandler, v);
        playerColor.put(v, new ReducedPlayer(v.getPlayer(), Color.values()[playerViewList.size()].toString()));

        v.addObserver(controller);
        game.addObserver(v);

        if (!reloaded)
            game.addPlayer(player);

        return true;
    }

    public boolean canStart() {
        int numOfPl;
        int size;

        synchronized (lockLobby) {
            numOfPl = numberOfPlayers;
        }

        synchronized (playerViewList) {
            size = playerViewList.size();
        }

        return size == numOfPl;
    }

    public boolean isCurrentPlayerInGame(ServerClientHandler c) {
        boolean ret;

        synchronized (game) {
            synchronized (playingConnection) {
                ret = game.getCurrentPlayer().nickName.equals(playingConnection.get(c).getPlayer());
            }
        }

        return ret;
    }

    public boolean isPresentInGame(ServerClientHandler c) {
        return playingConnection.get(c) != null;
    }

    public String getColor(ServerClientHandler c) {
        String color;

        synchronized (playerColor) {
            synchronized (playingConnection) {
                color = playerColor.get(playingConnection.get(c)).getColor();
            }
        }

        return color;
    }
}
