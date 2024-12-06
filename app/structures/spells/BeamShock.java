package structures.spells;

import java.util.Random;
import java.util.List;
import java.util.Comparator;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Abilities;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.interfaces.*;

/**
 * This is a representation of the Beam Shock spell. 
 * The spell card has a unique ID. The spell card has a 
 * set mana cost. It utilizes the Spell interface. 
 *
 */

public class BeamShock implements Spell {
    private int id;
    private int cost;

    public BeamShock() {
        this.id = 22;
        this.cost = 0;
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
        BasicCommands.addPlayer2Notification(out, "Casting Beamshock!!!", 5);
        try { Thread.sleep(3000); } catch (Exception e) { e.printStackTrace(); }
        
        List<Unit> enemies = gameState.getPlayer1Units();
        Unit unit = new Unit();
        Tile tile = new Tile();
        unit = enemies.stream().max(Comparator.comparingInt(Unit::getMaxHealth)).orElse(null);
        boolean picked = false;
        while(!picked) {
            // pick a random num
            Random rand = new Random();
            int randNum = rand.nextInt(enemies.size());
            unit = enemies.get(randNum);
            if (unit != gameState.getPlayer1().getAvatar()) {
                tile = unit.getTile(gameState);
                picked = true;
            }
        }

        unit.setBeamshocked(true);
    }
}