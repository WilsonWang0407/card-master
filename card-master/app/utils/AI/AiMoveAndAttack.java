package utils.AI;

import akka.actor.ActorRef;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

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

public class AiMoveAndAttack {
    public static void moveAndAttack(ActorRef out, GameState gameState) {
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        Unit toMove = null;

        for (Unit u: gameState.getPlayer2Units()) {
            if (u.canMove() && !u.getJustSummoned()) {
                List<Tile> validTiles = u.getValidMovementTiles(gameState);
                if (validTiles.isEmpty()) continue;
                List<Tile> validEnemyTiles = u.getAttackableEnemyTiles(gameState);
                // no valid enemy > randomly move
                if (validEnemyTiles.isEmpty()) {
                    if (!validTiles.isEmpty()) {
                        // choose a random tile to move to
                        Random rand = new Random();
                        int randNum = rand.nextInt(validTiles.size());
                        Tile targetTile = validTiles.get(randNum);

                        // move
                        u.moveUnit(targetTile, 2, out, gameState);
                    }
                }

                // there's one up enemy in its range > attack
                else {
                    // choose a random enemyTile to attack
                    Random rand = new Random();
                    int randNum = rand.nextInt(validEnemyTiles.size());
                    Tile targetTile = validEnemyTiles.get(randNum);
                                                    
                    for (Tile t: validEnemyTiles) {
                        if (t.getUnit() == player1.getAvatar()) {
                            targetTile = t;
                        }
                    }

                    // this enemy is adjacent > just attack
                    if (u.getSurroundingTiles(gameState).contains(targetTile)) {
                        Unit defendingUnit = targetTile.getUnit();
                        u.attackUnit(defendingUnit, 2, gameState, out);
                        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    else {
                        // 1. move to the nearest tile
                        Tile firstPriorityTile = null;

                        for (Tile t: validTiles) {
                            if (targetTile.isHasUnit()) {
                                if (targetTile.getUnit().getSurroundingTiles(gameState).contains(t)) {
                                    if (
                                        t.getTilex() - targetTile.getTilex() == 0 && t.getTiley() - targetTile.getTiley() != 0 ||
                                        t.getTilex() - targetTile.getTilex() != 0 && t.getTiley() - targetTile.getTiley() == 0
                                    ) {
                                        firstPriorityTile = t;
                                    }
                                }
                            }
                        }

                        if (firstPriorityTile != null) {
                            u.moveUnit(firstPriorityTile, 2, out, gameState);
                            try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }

                        } else {
                            for (Tile t: validTiles) {
                                if (targetTile.isHasUnit()) {
                                    if (targetTile.getUnit().getSurroundingTiles(gameState).contains(t)) {
                                        u.moveUnit(t, 2, out, gameState);
                                        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
                                        break;
                                    }
                                }
                            }
                        }

                        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

                        // 2. attack
                        Unit defendigUnit = targetTile.getUnit();
                        u.attackUnit(defendigUnit, 2, gameState, out);
                    }
                }
            }
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        
    }
}

