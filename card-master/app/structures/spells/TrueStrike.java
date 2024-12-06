package structures.spells;

import java.util.Random;
import java.util.List;

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
 * This is a representation of the True Strike spell. 
 * The spell card has a unique ID. The spell card has a 
 * set mana cost. It utilizes the Spell interface. 
 *
 */

public class TrueStrike implements Spell {
    private int id;
    private int cost;

    public TrueStrike() {
        this.id = 21;
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
        BasicCommands.addPlayer2Notification(out, "Casting TrueStrike!!!", 5);
        try { Thread.sleep(3000); } catch (Exception e) { e.printStackTrace(); }
        
        List<Unit> enemies = gameState.getPlayer1Units();        
        // pick a random num
        Random rand = new Random();
        int randNum = rand.nextInt(enemies.size());
        Unit unit = enemies.get(randNum);
        Tile tile = unit.getTile(gameState);

        // deal damage to target unit
        // backend:
        int damage = 2;
        int restHealth = (unit.getHealth() - damage > 0) ? unit.getHealth() - damage
                : 0;
        unit.setHealth(restHealth);

        // frontend:
        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.setUnitHealth(out, unit, unit.getHealth());
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        if (unit == player1.getAvatar()) {
            player1.setHealth(unit.getHealth());
            BasicCommands.setPlayer1Health(out, gameState.getPlayer1());
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
            if (unit.getArtifact() > 0) {
                // summon a wraithling 
                List<Tile> surroundingTiles = unit.getSurroundingTiles(gameState);
                for (Tile t: surroundingTiles) {
                    if (!t.isHasUnit()) {
                        t.summonWraithling(out, gameState);
                        break;
                    }
                }
                unit.setArtifact(unit.getArtifact()-1);
            }
        } else if (unit == player2.getAvatar()) {
            player2.setHealth(unit.getHealth());
            BasicCommands.setPlayer2Health(out, gameState.getPlayer2());
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);

        // check if target unit is dead
        if (unit.isDead()) {
            List<Unit> units = unit.getPlayerId() == 1 ? gameState.getPlayer2Units() : gameState.getPlayer1Units();
            // backend:
            tile.setHasUnit(false);
            tile.setUnit(null);
            units.remove(unit);

            // frontend:
            BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Abilities.deathwatch(out, gameState);

            BasicCommands.deleteUnit(out, unit);
            
            gameState.checkGameOverCondition(out);

        }
    }
}