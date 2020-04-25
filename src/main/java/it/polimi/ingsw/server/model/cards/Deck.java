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

import it.polimi.ingsw.server.model.cards.xml.GodParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

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

        //test();
    }

    /*public void test() {
        List<God> gods = new ArrayList<God>();
        gods.add(God.APOLLO);
        gods.add(God.ZEUS);

        fetchCards(gods);
    }*/

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

    public void fetchCard(God god) {
        parser.parseCards(Collections.singletonList(god));
    }

    public void fetchCards(List<God> gods) {
        parser.parseCards(gods);
    }

    public void fetchDeck() {
        parser.parseCards(Arrays.asList(God.values()));
    }
}