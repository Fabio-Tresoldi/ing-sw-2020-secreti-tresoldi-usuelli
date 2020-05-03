package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class CLIScanner<S> {

    private final Scanner in;
    private final CLIPrinter<S> out;

    private static final String CONNECT = "Do you want to create a lobby or join an existing one: 1-create, 2-join\n";
    private static final String CREATEGAME = "Insert the number of players:\n";
    private static final String ASKLOBBY = "Insert a lobby's id:\n";
    private static final String CHOOSEDECK = "Insert the name of one the gods which will be used in this match: [godName]\n";
    private static final String CHOOSECARD = "Insert the name of the chosen god [godName]\n";
    private static final String CHOOSESTARTER = "Insert the name of the starter: [playerName]";
    private static final String PLACEWORKERS = "Insert the initial locations of your worker: [x,y]\n";
    private static final String CHOOSEWORKERS = "Select a worker: [x,y]\n";
    private static final String ACTION = "Make your action [x,y]\n";

    private final Map<DemandType, String> messageMap;
    private final Map<DemandType, Function<String, Boolean>> toRepeatMap;
    private final Map<DemandType, Function<Integer, Boolean>> indexMap;
    private final Map<DemandType, Function<String, Boolean>> toUsePowerMap;
    private final Map<DemandType, Function<String, S>> payloadMap;
    private final ClientModel<S> clientModel;

    public CLIScanner(InputStream inputStream, CLIPrinter<S> out, ClientModel<S> clientModel) {
        in = new Scanner(inputStream);
        this.out = out;
        this.clientModel = clientModel;


        messageMap = new EnumMap<>(DemandType.class);
        toRepeatMap = new EnumMap<>(DemandType.class);
        indexMap = new EnumMap<>(DemandType.class);
        toUsePowerMap = new EnumMap<>(DemandType.class);
        payloadMap = new EnumMap<>(DemandType.class);


        messageMap.put(DemandType.CONNECT, CONNECT);
        messageMap.put(DemandType.CREATE_GAME, CREATEGAME);
        messageMap.put(DemandType.ASK_LOBBY, ASKLOBBY);
        messageMap.put(DemandType.CHOOSE_DECK, CHOOSEDECK);
        messageMap.put(DemandType.CHOOSE_CARD, CHOOSECARD);
        messageMap.put(DemandType.CHOOSE_STARTER, CHOOSESTARTER);
        messageMap.put(DemandType.PLACE_WORKERS, PLACEWORKERS);
        messageMap.put(DemandType.CHOOSE_WORKER, CHOOSEWORKERS);
        messageMap.put(DemandType.MOVE, ACTION);
        messageMap.put(DemandType.BUILD, ACTION);
        messageMap.put(DemandType.USE_POWER, ACTION);

        toRepeatMap.put(DemandType.CREATE_GAME, index -> Integer.parseInt(index) < 2 || Integer.parseInt(index) > 3);
        toRepeatMap.put(DemandType.CHOOSE_DECK, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_CARD, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_STARTER, clientModel::checkPlayer);
        toRepeatMap.put(DemandType.PLACE_WORKERS, value -> !clientModel.getReducedCell(value).isFree());
        toRepeatMap.put(DemandType.CHOOSE_WORKER, clientModel::checkWorker);
        toRepeatMap.put(DemandType.MOVE, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.BUILD, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.USE_POWER, clientModel::evalToRepeat);

        indexMap.put(DemandType.CHOOSE_DECK, index -> index < clientModel.getOpponents().size());
        indexMap.put(DemandType.PLACE_WORKERS, index -> index < 1);

        toUsePowerMap.put(DemandType.USE_POWER, clientModel::evalToUsePower);

        payloadMap.put(DemandType.CONNECT, this::parseString);
        payloadMap.put(DemandType.CREATE_GAME, this::parseString);
        payloadMap.put(DemandType.ASK_LOBBY, this::parseString);
        payloadMap.put(DemandType.CHOOSE_DECK, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_CARD, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_STARTER, this::parseString);
        payloadMap.put(DemandType.PLACE_WORKERS, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.CHOOSE_WORKER, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.MOVE, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.BUILD, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.USE_POWER, this::parseStringReducedDemandCell);
    }


    private S parseString(String string) {
        return (S) (new ReducedMessage(string));
    }

    private S parseStringReducedDemandCell(String string) {
        int x = string.charAt(0) - 48;
        int y = string.charAt(2) - 48;

        return (S) (new ReducedDemandCell(x, y));
    }

    private S parseStringGod(String string) {
        return (S) (God.parseString(string));
    }

    public Demand<S> requestInput(DemandType demandType) {
        boolean toRepeat;
        boolean toUsePower;
        boolean incrementIndex;
        int i = 0;
        String value;
        List<S> payloadList = new ArrayList<>();
        S payload = null;

        Function <String, Boolean> toRepeatFunction;
        Function <Integer, Boolean> indexFunction;
        Function <String, Boolean> powerFunction;
        Function <String, S> payloadFunction;

        do {
            toRepeat = false;
            toUsePower = false;
            incrementIndex = false;

            out.printString(messageMap.get(demandType));
            value = in.nextLine();

            toRepeatFunction = toRepeatMap.get(demandType);
            if (toRepeatFunction != null)
                toRepeat = toRepeatFunction.apply(value);

            if (toRepeat)
                out.printError();
            else {
                powerFunction = toUsePowerMap.get(demandType);
                if (powerFunction != null)
                    toUsePower = powerFunction.apply(value);

                payloadFunction = payloadMap.get(demandType);
                if (payloadFunction != null) {
                    payload = payloadMap.get(demandType).apply(value);
                    payloadList.add(payload);
                }

                indexFunction = indexMap.get(demandType);
                if (indexFunction != null)
                    incrementIndex = indexMap.get(demandType).apply(i);

                if (incrementIndex)
                    i++;
            }
        } while (toRepeat || incrementIndex);

        if (toUsePower)
            return new Demand<>(DemandType.USE_POWER, payload);

        if (i > 0)
            return new Demand<>(demandType, (S) payloadList);

        return new Demand<>(demandType, payload);
    }
}
