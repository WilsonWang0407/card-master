package structures.spells;

import java.util.List;
import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Board;
import structures.basic.Player;
import structures.interfaces.Spell;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This is a representation of the Wraithling Swarm spell. 
 * The spell card has a unique ID. The spell card has a 
 * set mana cost. It utilizes the Spell interface. 
 *
 */

public class WraithlingSwarm implements Spell {

    private int id = 11;
    private int cost = 3;

    public WraithlingSwarm(){
        this.id = 11;
        this.cost = 3;
    }

    @Override
    public void cast(Player caster, Unit targetUnit, Tile targetTile, ActorRef out, GameState gameState) {

        int i = targetTile.getTilex();
        int j = targetTile.getTiley();
        Board board = gameState.getBoard();

        if (!targetTile.isHasUnit()) {
            // Check for horizontal summoning, ensuring boundaries are respected
            if (j > 0 && j < 5 - 1) { // Adjust according to your board dimensions
                if (!board.getTile(i, j-1).isHasUnit() && !board.getTile(i, j+1).isHasUnit()) {
                    board.getTile(i, j-1).summonWraithling(out, gameState);
                    targetTile.summonWraithling(out, gameState);
                    board.getTile(i, j+1).summonWraithling(out, gameState);
                }
            }
            
            // Check for vertical summoning, ensuring boundaries are respected
            else if (i > 0 && i < 9 - 1) { // Adjust according to your board dimensions
                if (!board.getTile(i-1, j).isHasUnit() && !board.getTile(i+1, j).isHasUnit()) {
                    board.getTile(i-1, j).summonWraithling(out, gameState);
                    targetTile.summonWraithling(out, gameState);
                    board.getTile(i+1, j).summonWraithling(out, gameState);
                }
            }
        }

    }
}
    

