package structures.spells;

import commands.BasicCommands;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.interfaces.Spell;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This is a representation of the Horn of the
 * Forsaken spell. The spell card has a unique ID. 
 * The spell card has a set mana cost. It utilizes 
 * the Spell interface. 
 *
 */

public class HornOfTheForsaken implements Spell {
    private int id = 10;
    private int cost = 1;

    public HornOfTheForsaken(){
        this.id = 10;
        this.cost = 1;
    }

    @Override
    public void cast(Player caster, Unit targetUnit, Tile targetTile, ActorRef out, GameState gameState) {
        int artifact = targetUnit.getArtifact() + 3;
        targetUnit.setArtifact(artifact);
    }
}