package it.polimi.ingsw.model.cards.powers.tags;

import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;

import java.util.ArrayList;
import java.util.List;

public class Malus {

    private MalusType malusType;
    private boolean permanent;
    private int numberOfTurns;
    private List<MalusLevel> direction;

    public Malus() {
        direction = new ArrayList<>();
    }

    public Malus(Malus malus) {
        direction = new ArrayList<>();

        malusType = malus.getMalusType();
        permanent = malus.isPermanent();
        numberOfTurns = malus.numberOfTurns;
        direction.addAll(malus.direction);
    }

    public MalusType getMalusType() {
        return malusType;
    }

    public void setMalusType(MalusType malusType) {
        this.malusType = malusType;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public void setNumberOfTurns(int numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public List<MalusLevel> getDirection() {

        return new ArrayList<>(direction);
    }

    public void addDirectionElement(MalusLevel malusDirectionElement) {
        direction.add(malusDirectionElement);
    }

}
