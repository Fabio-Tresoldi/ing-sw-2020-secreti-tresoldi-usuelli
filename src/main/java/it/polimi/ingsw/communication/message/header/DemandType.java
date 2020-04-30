package it.polimi.ingsw.communication.message.header;

public enum DemandType {
    CONNECT,
    RELOAD,
    CREATE_GAME,
    JOIN_GAME,
    ASK_LOBBY,
    WAIT,
    START,
    CHOOSE_DECK,
    CHOOSE_CARD,
    PLACE_WORKERS,
    CHOOSE_WORKER,
    MOVE,
    BUILD,
    USE_POWER,
    CONFIRM,
    UNDO;

    public static DemandType parseString(String str) {
        switch (str) {
            case "connect":
                return CONNECT;
            case "reload":
                return RELOAD;
            case "createGame":
                return CREATE_GAME;
            case "joinGame":
                return JOIN_GAME;
            case "start":
                return START;
            case "chooseDeck":
                return CHOOSE_DECK;
            case "chooseCard":
                return CHOOSE_CARD;
            case "placeWorkers":
                return PLACE_WORKERS;
            case "chooseWorker":
                return CHOOSE_WORKER;
            case "move":
                return MOVE;
            case "build":
                return BUILD;
            case "usePower":
                return USE_POWER;
            case "confirm":
                return CONFIRM;
            case "undo":
                return UNDO;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case CONNECT:
                return "connect";
            case RELOAD:
                return "reload";
            case CREATE_GAME:
                return "createGame";
            case JOIN_GAME:
                return "joinGame";
            case ASK_LOBBY:
                return "askLobby";
            case WAIT:
                return "wait";
            case START:
                return "start";
            case CHOOSE_DECK:
                return "chooseDeck";
            case CHOOSE_CARD:
                return "chooseCard";
            case PLACE_WORKERS:
                return "placeWorkers";
            case CHOOSE_WORKER:
                return "chooseWorker";
            case MOVE:
                return "move";
            case BUILD:
                return "build";
            case USE_POWER:
                return "usePower";
            case CONFIRM:
                return "confirm";
            case UNDO:
                return "undo";
            default:
                return "";
        }
    }
}
