package structures.spells;

import commands.BasicCommands;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.interfaces.Spell;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

/**
 * This is a representation of the Dark Terminus spell. 
 * The spell card has a unique ID. The spell card has a 
 * set mana cost. It utilizes the Spell interface. 
 *
 */

public class DarkTerminus implements Spell {
    private int id = 12;
    private int cost = 4;

    public DarkTerminus(){
        this.id = 12;
        this.cost = 4;
    }

    @Override
    public void cast(Player caster, Unit targetUnit, Tile targetTile, ActorRef out, GameState gameState) {
        /* 
            Destroy the enemy creature
        */
        // backend:
        targetTile.setUnit(null);
        targetTile.setHasUnit(false);
        gameState.getPlayer2Units().remove(targetUnit);

        // frontend:
        Reset.resetHighLightTiles(out, gameState);
        BasicCommands.playUnitAnimation(out, targetUnit, UnitAnimationType.death);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} // Allow time for the unit deletion to be displayed
        BasicCommands.deleteUnit(out, targetUnit);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        

        /* 
            Summon a Wraithling on the tile of the destroyed creature
        */ 
        // backend: 
        Unit wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, gameState.unitId++, Unit.class);
        wraithling.setPositionByTile(targetTile);
        wraithling.setPlayerId(1);
        wraithling.setAttack(1);
        wraithling.setHealth(1);
        targetTile.setUnit(wraithling);
        targetTile.setHasUnit(true);
        // add the unit in the recording arrayList
        gameState.getPlayer1Units().add(wraithling);
        

        // frontend:
        // Load the effect animation for summoning
        EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        // Play the summoning effect animation on the target tile
        BasicCommands.playEffectAnimation(out, effect, targetTile);
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        // Draw the summoned unit on the target tile
        BasicCommands.drawUnit(out, wraithling, targetTile);
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
        try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();} // Allow time for the wraithling summoning to be displayed
    }
}