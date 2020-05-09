/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.gods.GodParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.stream.Collectors;

public class Deck {
    /*@class
     * it contains a list of the 14 chosen cards
     */

    private final List<Card> cards;
    private final GodParser parser;
    private static final Random randomIndex = new Random();

    public Deck() throws ParserConfigurationException, SAXException {
        /*@constructor
         * it creates a list of the 14 chosen cards
         */

        parser = new GodParser(this);
        cards = new ArrayList<>();
    }

    public Card popRandomCard() {
        /*@function
         * it picks a card from the deck
         */

        Card pickedCard = null;

        if (!cards.isEmpty()) {
            pickedCard= cards.get(randomIndex.nextInt(cards.size()));
            cards.remove(pickedCard);
        }

        return pickedCard;
    }

    public void addCard(Card newCard) {
        this.cards.add(newCard);
    }

    public Card getCard(God god) {
        return cards.stream().filter(c -> c.getGod().equals(god)).reduce(null, (a,b) -> a != null ? a : b);
    }

    public void fetchCard(God god) {
        parser.parseCards(Collections.singletonList(god));
    }

    public void fetchCards(List<God> gods) {
        parser.parseCards(gods);
    }

    public void fetchDeck() {
        parser.parseCards(Arrays.asList(God.values()));
    }

    public Card popCard(God god) {
        /*@function
         * it picks the selected card from the deck
         */

        Card pickedCard = null;

        if (!cards.isEmpty()) {
            for (Card card : cards) {
                if (card.getGod().equals(god)){
                    pickedCard = card.clone();
                    cards.remove(pickedCard);
                }
            }
        }

        return pickedCard;
    }

    public List<ReducedCard> popAllGods(int numberOfPlayers) {
        fetchDeck();

        return cards.stream().filter(c -> c.getNumPlayer() >= numberOfPlayers).map(ReducedCard::new).collect(Collectors.toList());
    }

    public List<ReducedCard> popChosenGods(List<God> chosenGods) {
        List<ReducedCard> reducedCardList = new ArrayList<>();

        for (Card c : cards) {
            for (God g : chosenGods) {
                if (c.getGod().equals(g)) {
                    reducedCardList.add(new ReducedCard(c));
                    break;
                }
            }
        }

        return reducedCardList;
    }
}