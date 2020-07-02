package it.polimi.ingsw.server.model.cards.gods;

import it.polimi.ingsw.server.model.cards.Deck;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GodParser {
    private final String XMLFILE = "/xml/gods.xml";

    private final SAXParserFactory factory;
    private final SAXParser parser;
    private final GodHandler handler;

    private static final Logger LOGGER = Logger.getLogger(GodParser.class.getName());

    public GodParser(Deck deck) throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        handler = new GodHandler(deck);
    }

    public void parseCards(List<God> gods) {
        try {
            Collections.sort(gods);
            handler.setGods(gods);
            parser.parse(this.getClass().getResource(XMLFILE).toURI().toString(), handler);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "couldn't load gods");
        }
    }
}
