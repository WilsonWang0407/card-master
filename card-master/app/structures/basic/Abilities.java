package structures.basic;

import structures.GameState;
import structures.basic.Unit;
import akka.actor.ActorRef; 
import commands.BasicCommands;
import structures.creatures.player1.*;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Abilities {

    
     //Should call these methods whenever a unit dies

    //Bad Omen Deathwatch ability 
    public static void deathwatch(ActorRef out, GameState gameState) {
    // Loop through all units to find Bad Omen units
    for (Unit boardUnit : gameState.getAllUnits()) {
        if (boardUnit.getBackendId() == 3) {
            System.out.println(123123);
            // Increase the Bad Omen unit's attack
            boardUnit.setAttack(boardUnit.getAttack() + 1);
            // Update the attack value on the front end
            BasicCommands.setUnitAttack(out, boardUnit, boardUnit.getAttack());
            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        }
        if (boardUnit.getBackendId() == 6) { //shadow watcher deathwatch
            // Increase the Bad Omen unit's attack
            boardUnit.setAttack(boardUnit.getAttack() + 1);
            boardUnit.setHealth(boardUnit.getHealth() + 1);
            // Update the attack value on the front end
            BasicCommands.setUnitAttack(out, boardUnit, boardUnit.getAttack());
            BasicCommands.setUnitHealth(out, boardUnit, boardUnit.getHealth());
            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        }
        if (boardUnit.getBackendId() == 8) { //bloodmoon priestess deathwatch
            // Check all adjacent tiles
            List<Tile> adjacentTiles = new ArrayList<>();
            int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
            int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

            for (int direction = 0; direction < dx.length; direction++) {
                int newX = boardUnit.getPosition().getTilex() + dx[direction];
                int newY = boardUnit.getPosition().getTiley() + dy[direction];

                // Check if the adjacent tile is within board limits and not occupied
                if (newX >= 0 && newX < 9 && newY >= 0 && newY < 5) {
                    Tile adjacentTile = gameState.getBoard().getTile(newX, newY);
                    if (!adjacentTile.isHasUnit()) {
                        adjacentTiles.add(adjacentTile);
                    }
                }
            }

            // If there are unoccupied tiles, randomly select one and summon a Wraithling
            if (!adjacentTiles.isEmpty()) {
                Random rand = new Random();
                Tile selectedTile = adjacentTiles.get(rand.nextInt(adjacentTiles.size()));
                selectedTile.summonWraithling(out, gameState);
            }
        }
        if (boardUnit.getBackendId() == 9) {
            // Deal 1 damage to the enemy avatar and heal your avatar for 1
            Unit playerAvatar = gameState.getPlayer1().getAvatar();
            Unit enemyAvatar = gameState.getPlayer2().getAvatar();

            // Assuming player1's Shadowdancer caused the deathwatch effect:
            if (boardUnit.getPlayerId() == playerAvatar.getPlayerId()) {
                // Heal player's avatar
                playerAvatar.setHealth(playerAvatar.getHealth() + 1);
                BasicCommands.setUnitHealth(out, playerAvatar, playerAvatar.getHealth());
                
                // Damage enemy's avatar
                enemyAvatar.setHealth(enemyAvatar.getHealth() - 1);
                BasicCommands.setUnitHealth(out, enemyAvatar, enemyAvatar.getHealth());
            } 

            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        }
    }            
}
        

    public static void openingGambit(ActorRef out, GameState gameState){
        for (Unit boardUnit : gameState.getAllUnits()) {
            if (boardUnit.getBackendId() == 4) {
                int x = boardUnit.getPosition().getTilex() - 1;
                int y = boardUnit.getPosition().getTiley();
                if(y >= 0) {
                    Tile behindTile = gameState.getBoard().getTile(x, y);
                    if(!behindTile.isHasUnit()){
                        behindTile.summonWraithling(out, gameState);
                    }
                }
            }
            if (boardUnit.getBackendId() == 7) { //nightsorrow assassin opening gambit
                // Check surrounding tiles of the Nightsorrow Assassin
                int assassinX = boardUnit.getPosition().getTilex();
                int assassinY = boardUnit.getPosition().getTiley();
                
                // Define the range of adjacent squares (immediately around the assassin)
                int[] dx = {-1, 0, 1, 0};
                int[] dy = {0, 1, 0, -1};
    
                for (int direction = 0; direction < dx.length; direction++) {
                    int newX = assassinX + dx[direction];
                    int newY = assassinY + dy[direction];
    
                    // Check if the adjacent square is within board limits
                    if (newX >= 0 && newX < 9 && newY >= 0 && newY < 5) {
                        Tile adjacentTile = gameState.getBoard().getTile(newX, newY);
                        Unit targetUnit = adjacentTile.getUnit();
    
                        // Check if there's an enemy unit in the adjacent square and it is below its maximum health
                        if (targetUnit != null && targetUnit.getPlayerId() != boardUnit.getPlayerId() && 
                            targetUnit.getHealth() < targetUnit.getMaxHealth()) {
                        
                            // Destroy the enemy unit
                            adjacentTile.setUnit(null);
                            adjacentTile.setHasUnit(false);

                            BasicCommands.playUnitAnimation(out, targetUnit, UnitAnimationType.death);
                            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                            BasicCommands.deleteUnit(out, targetUnit);
                            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                        
                            // Exit after one unit is destroyed, since the ability only triggers once
                            return;
                        }
                    }
                }
            }
                        
        }
    }   
    }   
                    

    /*// Assuming this is within your game logic where units are removed/killed
public void removeUnit(Unit unit, ActorRef out, GameState gameState) {
    // Unit removal logic...
    
    // Trigger Deathwatch effects
    if (gameState != null) {
        for (Unit boardUnit : gameState.getAllUnits()) {
            if (boardUnit instanceof Deathwatch) {
                ((Deathwatch) boardUnit).onUnitDeath(unit, out, gameState);
            }
        }
    }
}
 */

    

