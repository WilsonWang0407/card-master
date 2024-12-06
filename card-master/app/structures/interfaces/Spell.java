package structures.interfaces;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This interface defines the contract for the spell classes.
 * Each spell utilizes the cast method to cast the spell.
 *
 */

public interface Spell {
    // Method to cast the spell
    void cast(Player caster, Unit target, Tile targetTile, ActorRef out, GameState gameState);
}