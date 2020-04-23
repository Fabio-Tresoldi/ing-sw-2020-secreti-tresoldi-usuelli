package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.God;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CLIPrinter {

    private static final String LOGO = "\n" +
            "  ______                             _       _ \n" +
            " / _____)             _             (_)     (_)\n" +
            "( (____  _____ ____ _| |_ ___   ____ _ ____  _ \n" +
            " \\____ \\(____ |  _ (_   _) _ \\ / ___) |  _ \\| |\n" +
            " _____) ) ___ | | | || || |_| | |   | | | | | |\n" +
            "(______/\\_____|_| |_| \\__)___/|_|   |_|_| |_|_|\n" +
            "                                               \n\n";

    private CLIPrinter() {
        throw new IllegalStateException("Utility class");
    }

    public static void printLogo(PrintStream out) {
        out.println(LOGO);
    }

    public static void printString(PrintStream out, String message) {
        out.print(message);
    }

    public static void printBoard(PrintStream out, ReducedAnswerCell[][] board, List<ReducedPlayer> opponents) {

        for (int i = 4; i >= 0; i--) {
            for (int j = 0; j <5; j++)
                printCell(out, board[i][j], opponents);
            out.print("\n");
        }
        out.print("\n");
    }

    private static void printCell(PrintStream out, ReducedAnswerCell cell, List<ReducedPlayer> opponents) {
        if (!cell.isFree()) {
            out.print(opponents.stream()
                    .filter(opponent -> opponent.getNickname().equals(cell.getWorker().getOwner()))
                    .map(ReducedPlayer::getColor)
                    .map(Color::parseString)
                    .filter(Objects::nonNull)
                    .map(Color::getEscape)
                    .reduce(Color.RESET, (a, b) -> !a.equals(Color.RESET)
                            ? a
                            : b
                    )
            );
        }
        out.print("[" + cell.getLevel().toInt() + "]" + Color.RESET);

    }

    public static void printOpponents(PrintStream out, List<ReducedPlayer> opponents) {
        out.print("Opponent");
        if (opponents.size() == 2) out.print("s");
        out.print(": ");

        out.println(opponents.stream()
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getNickname() + Color.RESET)
                .reduce(null, (a, b) -> a != null
                        ? a + ", " + b
                        : b
                )
        );

        out.print("\n");
    }

    public static void printGods(PrintStream out, List<God> godList) {
        out.print("Card");
        if (godList.size() > 1) out.print("s");
        out.print(": ");

        /*godList.stream()
               .map(God::toString)
               .filter(Objects::nonNull)
               .map(s -> s.subSequence(0, 1).toString() + s.toLowerCase().subSequence(1, s.length()).toString())
               .forEach(out::print);*/

        out.print(godList + "\n");
    }

    public static void printPossibleActions(PrintStream out, ReducedAnswerCell[][] reducedBoard) {
        List<ReducedAnswerCell> cellList = Arrays.stream(reducedBoard).flatMap(Arrays::stream).filter(x -> x.getAction() != ReducedAction.DEFAULT).collect(Collectors.toList());
        List<ReducedAction> reducedActions = cellList.stream().map(ReducedAnswerCell::getAction).distinct().collect(Collectors.toList());

        out.print("Action");
        if (reducedActions.size() >= 2) out.print("s");
        out.println(": ");
        for (ReducedAction action : reducedActions) {
            out.print(action.toString() + ": ");

            out.println(
                    cellList.stream()
                            .filter(c -> c.getAction().equals(action))
                            .map(c -> "(" + c.getX() + ", " +c.getY() + ")")
                            .reduce(null, (a, b) -> a != null
                                    ? a + ", " + b
                                    : b
                            )
            );
        }
    }
}

