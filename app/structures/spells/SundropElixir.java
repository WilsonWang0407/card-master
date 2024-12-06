package structures.spells;

import akka.actor.ActorRef;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.interfaces.Spell;

/**
 * This is a representation of the Sundrop Elixir spell. 
 * The spell card has a unique ID. The spell card has a 
 * set mana cost. It utilizes the Spell interface. 
 *
 */

public class SundropElixir implements Spell {
    private int id;
    private int cost;

    public SundropElixir() {
        this.id = 20;
        this.cost = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public void cast(Player caster, Unit target, Tile targetTile, ActorRef out, GameState gameState) {
        BasicCommands.addPlayer2Notification(out, "Casting Sundrop Elixir!!!", 5);
        try { Thread.sleep(3000); } catch (Exception e) { e.printStackTrace(); }

        List<Unit> units = gameState.getPlayer2Units();
        Unit unit = units.stream().max(Comparator.comparingInt(u -> u.getMaxHealth() - u.getHealth())).orElse(null);

        // cast spell on allied unit (heal 4 health)
        if (unit.getHealth() + 4 >= unit.getMaxHealth()) {
            unit.setHealth(unit.getMaxHealth());

        } else {
            unit.setHealth(unit.getHealth() + 4);
        }
    }
}