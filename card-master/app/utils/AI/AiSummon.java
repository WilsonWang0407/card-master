package utils.AI;

import akka.actor.ActorRef;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.creatures.player2.*;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

public class AiSummon {
    public static void summon(ActorRef out, GameState gameState) {
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        Card toSummon = null;
		boolean done= false;
        while (!done) {
            List<Card> validCards = new ArrayList<Card>();
            for (Card card : player2.getHands()) {
                int maxManaCost = player2.getMana();
                
                if (card.getManacost() <= maxManaCost && card.isCreature()) {
                    validCards.add(card);
                }
            }
            
            if (validCards.isEmpty()) done = true;
            
            toSummon = validCards.stream().max(Comparator.comparingInt(Card::getManacost)).orElse(null);
    
            //Choose a random valid tile and summon if possible
            if (toSummon != null) {
                List<Tile> validTiles = findValidTilesForSummoning(gameState);
                if (!validTiles.isEmpty()) {
                    EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
                    Random random = new Random();
                    Tile chosenTile = validTiles.get(random.nextInt(validTiles.size()));
                    Unit unit = BasicObjectBuilders.loadUnit(toSummon.getUnitConfig(), gameState.unitId++, Unit.class);
                    // Set unit's attack & health
                    Unit summonUnit = new Unit();
                    if(toSummon.getCardname().equals("Skyrock Golem")){
                        summonUnit = new SkyrockGolem();
                    }
                    else if(toSummon.getCardname().equals("Swamp Entangler")){
                        summonUnit = new SwampEntangler();
                    }
                    else if(toSummon.getCardname().equals("Silverguard Knight")){
                        summonUnit = new SilverguardKnight();
                    }
                    else if(toSummon.getCardname().equals( "Saberspine Tiger")){
                        summonUnit = new SaberspineTiger();
                        unit.setHasAttacked(false); // ability for rush
                    }
                    else if(toSummon.getCardname().equals("Young Flamewing")){
                        summonUnit = new YoungFlamewing();
                        // for flying ability
                        boolean foundATile = false;
                        for (Unit u: gameState.getPlayer1Units()) {
                            List<Tile> surroundingTile = u.getSurroundingTiles(gameState);
                            if (!surroundingTile.isEmpty()) {
                                for(Tile t: surroundingTile) {
                                    if (surroundingTile.get(0) != null) {
                                        chosenTile = t;
                                        foundATile = true;
                                        break;
                                    }
                                }
                            }

                            if (foundATile) break;
                        }
                    }
                    else if(toSummon.getCardname().equals("Silverguard Squire")){
                        summonUnit = new SilverguardSquire();
                    }
                    else if(toSummon.getCardname().equals("Ironcliff Guardian")){
                        summonUnit = new IroncliffeGuardian();
                    }
                    
                    // Set playerId for unit
                    unit.setPlayerId(2);
    
                    // Set the position of the unit to the target tile
                    unit.setPositionByTile(chosenTile); 
                    
                    // set unit on the tile
                    chosenTile.setUnit(unit);
                    chosenTile.setHasUnit(true);
    
                    // add the unit in the recording arrayList
                    gameState.getPlayer2Units().add(unit);
                    
                    // Play the summoning effect animation on the target tile
                    BasicCommands.playEffectAnimation(out, effect, chosenTile);
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    
                    // Draw the summoned unit on the target tile
                    BasicCommands.drawUnit(out, unit, chosenTile);
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

                    BasicCommands.addPlayer2Notification(out, toSummon.getCardname(), 3);
                    BasicCommands.setUnitAttack(out, unit, summonUnit.getAttack());

                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    unit.setAttack(summonUnit.getAttack());
    
                    BasicCommands.setUnitHealth(out, unit, summonUnit.getHealth());
                    try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    unit.setHealth(summonUnit.getHealth());
    
                    int position = player2.getHands().indexOf(toSummon);
                    
                    // Delete the card from the player's hand
                    List<Card> hands = player2.getHands();
                    hands.remove(toSummon);
    
                    player2.useMana(toSummon.getManacost()); // Deduct mana cost
                    BasicCommands.setPlayer2Mana(out, player2);
                }
            }
        }
		
        
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }

    public static List<Tile> findValidTilesForSummoning(GameState gameState) {
	       
    	List<Tile> validTiles = new ArrayList<Tile>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tile = gameState.getBoard().getTile(i, j);
                if (tile.getUnit() != null && tile.getUnit().getPlayerId() == 2) {
                    // Highlight valid tiles
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int x = i + dx;
                            int y = j + dy;
                            if (x >= 0 && x < 9 && y >= 0 && y < 5) {
                                Tile targetTile = gameState.getBoard().getTile(x, y);
                                if ( !validTiles.contains(targetTile) && !targetTile.isHasUnit() ) {
                                	validTiles.add(targetTile);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tile = gameState.getBoard().getTile(i, j);
                if (tile.getUnit() != null && tile.getUnit().getPlayerId() == 2) {
                    if (validTiles.contains(tile)) {
                    	validTiles.remove(tile);
                    }
                }
            }
        }
        
        return validTiles;

    }
}
