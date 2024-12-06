package events;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Abilities;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.creatures.player1.*;
import structures.spells.DarkTerminus;
import structures.spells.HornOfTheForsaken;
import structures.spells.WraithlingSwarm;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices
 * of the tile that was clicked. Tile indices start at 1.
 * 
 * {
 * messageType = “tileClicked”
 * tilex = <x index of the tile>
 * tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        /*
         * Strategy:
         * -- Clicked a unit > Click the tile
         *     --> Clicked Tile Does Not Has a Unit
         *         --> if the tile `canMove()` && `isValidMoveRange()` > Move the clicked unit to the tile
         *     --> Clicked Tile Has a Unit `u`
         *         --> if it is an `enemy` && `isValidAttackRange` > Attack
         * -- Haven't click a unit > Click the tile
         *     --> Have clicked a card > Summon/Cast card
         *         --> if clickedTile doesn't has a unit > summoning/wraithlingswarm
         *         --> if clickedTile has a creature > deathterminus && hornoftheforsaken
         *     --> Haven't clicked a card
         *         --> `canMove()` && `canAttack()` condition highlighting
         *         --> Clicked Tile Has a Unit -> Store in `gameState` 
         */
        
        

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();
        // Get the clicked tile
        Tile clickedTile = gameState.board.getTile(tilex, tiley);
        int currentPlayerId = gameState.player1sTurn? 1 : 2;

        if (gameState.player1sTurn == true) {

            // Have Clicked a Unit Before
            if (gameState.getLastClickedUnit() != null) {
                gameState.getLastClickedUnit().checkBeamshocked();

                // moving
                if (!clickedTile.isHasUnit()) {
                    Unit movingUnit = gameState.getLastClickedUnit();
                    movingUnit.moveUnit(clickedTile, currentPlayerId, out, gameState);
                    System.out.println("Unit Moved !!");  
                }
                
                
                // attacking
                else {
                    // reset the highlight tiles first
                    Reset.resetHighLightTiles(out, gameState);

                    Unit attackingUnit = gameState.getLastClickedUnit();

                    if (attackingUnit.canMove() && attackingUnit.canAttack()) {
                        List<Tile> validTiles = attackingUnit.getValidMovementTiles(gameState);
                        List<Tile> validEnemyTiles = attackingUnit.getAttackableEnemyTiles(gameState);
                        
                        if (validEnemyTiles.contains(clickedTile)) {
                            // if the enemy is adjacent
                            if (attackingUnit.getSurroundingTiles(gameState).contains(clickedTile)) {
                                // just attack
                                Unit defendingUnit = clickedTile.getUnit();
                                attackingUnit.attackUnit(defendingUnit, currentPlayerId, gameState, out);
                            }

                            else {
                                // 1. move to the nearest tile
                                // check the moved tile is direct
                                Tile firtPriorityTile = null;
                                Tile secPriorityTile = null;
                                for (Tile t: validTiles) {
                                    if (clickedTile.getUnit().getSurroundingTiles(gameState).contains(t)) {
                                        if (
                                            (t.getTilex() - clickedTile.getTilex() == 0 && t.getTiley() - clickedTile.getTiley() != 0 ) || 
                                            (t.getTilex() - clickedTile.getTilex() != 0 && t.getTiley() - clickedTile.getTiley() == 0 )
                                        ) {
                                            firtPriorityTile = t;
                                        }
                                    }         
                                }

                                for (Tile t: validTiles) {
                                    if (clickedTile.getUnit().getSurroundingTiles(gameState).contains(t)) {
                                            secPriorityTile = t;
                                    } 
                                }

                                if (firtPriorityTile != null) {
                                    attackingUnit.moveUnit(firtPriorityTile, currentPlayerId, out, gameState);
                                } else {
                                    attackingUnit.moveUnit(secPriorityTile, currentPlayerId, out, gameState);
                                }

                                try { Thread.sleep(2500); } catch (InterruptedException e) { e.printStackTrace(); }

                                // 2. attack
                                Unit defendingUnit = clickedTile.getUnit();
                                attackingUnit.attackUnit(defendingUnit, currentPlayerId, gameState, out);
                            }
                        }
                    }

                    if (!attackingUnit.canMove() && attackingUnit.canAttack()) {
                        List<Tile> validEnemyTiles = attackingUnit.getAttackableEnemyTiles(gameState);
                        if (validEnemyTiles.contains(clickedTile)) {
                            // if the enemy is adjacent
                            if (attackingUnit.getSurroundingTiles(gameState).contains(clickedTile)) {
                                // just attack
                                Unit defendingUnit = clickedTile.getUnit();
                                attackingUnit.attackUnit(defendingUnit, currentPlayerId, gameState, out);
                            }
                        }
                    }
                    gameState.setLastClickedUnit(null);
                }
            } 
            
            // Haven't Clicked a Unit Before
            else {
                
                // Clicked a card > Click the tile: 
                //    LastClickedCard.isCreature():   SUMMONING!
                //    ! LastClickedCard.isCreature(): CASTING SPELL!
                if (gameState.getLastClickedCard() != null) {
                    // check if there is a Unit on this tile
                    // Get the clicked card from the GameState
                    Card clickedCard = gameState.getLastClickedCard();

                    // Get the current player from the GameState
                    Player currentPlayer = gameState.player1;

                    // a boolean to check this card is used
                    boolean isUsed = false;
                    
                    if (!clickedTile.isHasUnit()) {
                        // Summoning
                        if (clickedCard.isCreature()) {
                            // Check if the clicked card and current player are not null
                            if (clickedCard != null && currentPlayer != null
                                    && clickedTile.validSummonTile(gameState, clickedTile, clickedCard) == true) {
                                // Load the effect animation for summoning
                                EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
                                
                                // Load the unit to be summoned (assuming the card specifies the unit)
                                Unit unit = BasicObjectBuilders.loadUnit(clickedCard.getUnitConfig(), gameState.unitId++, Unit.class);
                                
                                // Set playerId for unit
                                unit.setPlayerId(1);

                                // Set the position of the unit to the target tile
                                unit.setPositionByTile(clickedTile); 
                                
                                // set unit on the tile
                                clickedTile.setUnit(unit);
                                clickedTile.setHasUnit(true);

                                // add the unit in the recording arrayList
                                gameState.getPlayer1Units().add(unit);
                                
                                // Play the summoning effect animation on the target tile
                                BasicCommands.playEffectAnimation(out, effect, clickedTile);
                                
                                // Draw the summoned unit on the target tile
                                BasicCommands.drawUnit(out, unit, clickedTile);
                                try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

                                // Set unit's attack & health
                                Unit summonUnit = new Unit();
                                if(clickedCard.getCardname().equals("Bad Omen")){
                                    summonUnit = new BadOmen();
                                    unit.setBackendId(3);
                                }
                                else if(clickedCard.getCardname().equals("Gloom Chaser")){
                                    summonUnit = new GloomChaser();
                                    unit.setBackendId(4);
                                }
                                else if(clickedCard.getCardname().equals("Shadow Watcher")){
                                    summonUnit = new ShadowWatcher();
                                    unit.setBackendId(6);
                                }
                                else if(clickedCard.getCardname().equals( "Nightsorrow Assassin")){
                                    summonUnit = new NightsorrowAssassin();
                                    unit.setBackendId(7);
                                }
                                else if(clickedCard.getCardname().equals("Rock Pulveriser")){
                                    summonUnit = new RockPulveriser();
                                    unit.setBackendId(5);
                                }
                                else if(clickedCard.getCardname().equals("Bloodmoon Priestess")){
                                    summonUnit = new BloodmoonPriestess();
                                    unit.setBackendId(8);
                                }
                                else if(clickedCard.getCardname().equals("Shadowdancer")){
                                    summonUnit = new Shadowdancer();
                                    unit.setBackendId(9);
                                }
                                BasicCommands.setUnitAttack(out, unit, summonUnit.getAttack());
                                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                                unit.setAttack(summonUnit.getAttack());

                                BasicCommands.setUnitHealth(out, unit, summonUnit.getHealth());
                                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                                unit.setHealth(summonUnit.getHealth());

                                isUsed = true;
                                if (!clickedCard.getCardname().equals("Gloom Chaser")) Abilities.openingGambit(out, gameState);
                            }
                        }

                        // Casting wraithlingswarm
                        else {
                            if (clickedCard.getCardname().equals("Wraithling Swarm")) {
                                // check if the clicked Tile has a valid range of summoning space
                                List<Tile> validTiles = gameState.getValidWraithlingSwarmTiles();

                                if (!validTiles.isEmpty() && validTiles.contains(clickedTile)) {
                                    WraithlingSwarm spell = new WraithlingSwarm();
                                    spell.cast(currentPlayer, new Unit(), clickedTile, out, gameState);
                                    isUsed = true;
                                } 
                            }
                        }
                    }
                    // for deathterminus and hornoftheforsaken
                    else {
                        Unit unit = clickedTile.getUnit();
                        if (clickedCard.getCardname().equals("Horn of the Forsaken")) {
                            Unit avatar = currentPlayer.getAvatar();
                            if (unit == avatar) {
                                HornOfTheForsaken spell = new HornOfTheForsaken();
                                spell.cast(currentPlayer, unit, clickedTile, out, gameState);
                                isUsed = true;
                            }
                        }

                        else if (clickedCard.getCardname().equals("Dark Terminus")) {
                            // the target is an enemy
                            if (gameState.getPlayer2Units().contains(unit) && unit != gameState.getPlayer2().getAvatar()) {
                                DarkTerminus spell = new DarkTerminus();
                                spell.cast(currentPlayer, unit, clickedTile, out, gameState);
                                isUsed = true;
                            }
                        }
                    }

                    // Deal with the hand
                    if (isUsed) {
                        //Find the position of the clicked card in the player's hand
                        int position = currentPlayer.getHands().indexOf(clickedCard);
                                                        
                        // Delete the card from the player's hand
                        List<Card> hands = currentPlayer.getHands();
                        hands.remove(clickedCard);
                        BasicCommands.deleteCard(out, hands.size() + 1);
                        for (Card card: hands) {
                            currentPlayer.displayCard(out, card);
                        }

                        // Deduct mana
                        currentPlayer.setMana(currentPlayer.getMana() - clickedCard.getManacost());
                        BasicCommands.setPlayer1Mana(out, currentPlayer);
                        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    Reset.resetHighLightTiles(out, gameState);
                    // Reset last clicked card
                    gameState.setLastClickedCard(null);
                }

                // Haven't Clicked a card and Clicked a Tile
                // --> Wanna Click a unit
                else {

                    if (clickedTile.isHasUnit()) {
                        Unit clickedUnit = clickedTile.getUnit();

                        // For User Warning:
                        if (!clickedUnit.canMove() && !clickedUnit.canAttack()) {
                            BasicCommands.addPlayer1Notification(out, "This unit has already moved!!", 3);
                        }
    
                        if (clickedUnit.getJustSummoned() && clickedUnit.getPlayerId() == 1) {
                            BasicCommands.addPlayer1Notification(out, "This unit is just summoned!!", 3);
                        }

                        // Clicked Unit canMove && canAttack
                        if (
                            clickedUnit.canMove() && 
                            clickedUnit.canAttack() && 
                            !clickedUnit.getJustSummoned()
                        ) {
                            // Access the Board from GameState
                            Board board = gameState.getBoard();
    
                            if (clickedUnit.getPlayerId() == currentPlayerId) {
                                // Highlight valid movement tiles
                                // clickedTile.highlightValidMovementTiles(out, clickedUnit, board);

                                List<Tile> validTiles = clickedUnit.getValidMovementTiles(gameState);
                                for (Tile t: validTiles) {
                                    t.highlightValidTile(out);
                                }

                                // get valid enemy tiles and highlight
                                List<Tile> validEnemyTiles = clickedUnit.getAttackableEnemyTiles(gameState);
                                
                                for (Tile t: validEnemyTiles) {
                                    t.highlightInvalidTile(out);
                                }

                            } else {
                                // Send message to the front end that it is not the player's unit
                                BasicCommands.addPlayer1Notification(out, "This is not your unit.", 3);
                                try { Thread.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
                            }
                        }

                        // Clicked Unit Cannot Move but still can attack
                        else if (
                            !clickedUnit.canMove() && 
                            clickedUnit.canAttack() && 
                            !clickedUnit.getJustSummoned()
                        ) {
                            System.out.println(123);
                            // Access the Board from GameState
                            Board board = gameState.getBoard();
    
                            if (clickedUnit.getPlayerId() == currentPlayerId) {
                                // get valid enemy tiles and highlight
                                List<Tile> validEnemyTiles = clickedUnit.getAttackableEnemyTiles(gameState);
                                
                                for (Tile t: validEnemyTiles) {
                                    t.highlightInvalidTile(out);
                                }
                            } else {
                                // Send message to the front end that it is not the player's unit
                                BasicCommands.addPlayer1Notification(out, "This is not your unit.", 3);
                                try { Thread.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
                            }                            
                        }
                        gameState.setLastClickedUnit(clickedUnit);
                    }
                }
            }
        }
    }
}