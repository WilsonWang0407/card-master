package structures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.creatures.player1.*;
import utils.AI.*;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;
import commands.BasicCommands;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import akka.actor.ActorRef;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	public boolean gameInitialized = false;
	public boolean player1sTurn = false;
	public boolean player2sTurn = false;
	public Board board;
	public Player player1; // human player
	public Player player2; // AI player
	public int unitId = 0;
	public boolean isGameOver = false;

	// to record the units currently on the board
	public ArrayList<Unit> player1Units;
	public ArrayList<Unit> player2Units;
 
	private Player currentPlayer;
	//=0 makes it so player 1 stays at 2 mana for turn 1 & 2 then starts adding 1
	//=1 makes it so player 2 gets 3 mana on turn 1 instead of only 2, but player 1 increments correctly
	private Card lastClickedCard;
	private Unit lastClickedUnit;
	private Tile lastClickedTile;
	
	public GameState() {
		player1 = new Player(1);
		player2 = new Player(2);
		currentPlayer = player1;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Board getBoard() {
		return board;
	}
	
	public Card getLastClickedCard() {
        return lastClickedCard;
    }

    public void setLastClickedCard(Card lastClickedCard) {
        this.lastClickedCard = lastClickedCard;
    }

	public Unit getLastClickedUnit() {
		return this.lastClickedUnit;
	}

	public void setLastClickedUnit(Unit lastClickedUnit) {
		this.lastClickedUnit = lastClickedUnit;
	}

	public Tile getLastClickedTile() {
		return this.lastClickedTile;
	}

	public void setLastClickedTile(Tile lastClickedTile) {
		this.lastClickedTile = lastClickedTile;
	}

	public void setPlayer1Avatar (Unit avatar) {
		this.player1.setAvatar(avatar);
	}
	public void setPlayer2Avatar (Unit avatar) {
		this.player2.setAvatar(avatar);
	}
	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public ArrayList<Unit> getPlayer1Units() {
		return this.player1Units;
	}

	public ArrayList<Unit> getPlayer2Units() {
		return this.player2Units;
	}

	public void player2CastSpell(ActorRef out, GameState gameState) {
		AiCastSpell.castSpell(out, gameState);
	}
	public void player2Summon(ActorRef out, GameState gameState) {
		AiSummon.summon(out, gameState);
	}

	public void player2MoveAndAttack(ActorRef out, GameState gameState) {
		AiMoveAndAttack.moveAndAttack(out, gameState);
	}

	public void player2EndTurn(ActorRef out, GameState gameState) {
		AiEndTurn.endturn(out, gameState);
	} 

	public void setGameOver (boolean gameOver) {
		this.isGameOver = gameOver;
	}
	public boolean isGameOver() { //checking health for game over condition
		return player1.getHealth() <= 0 || player2.getHealth() <= 0;
	}
	public Player getWinner() { //checking health for winner of game
		if (player1.getHealth() <= 0) return player2;
		if (player2.getHealth() <= 0) return player1;
		return null;
	}
	
    //checking if game is over based on health
	public void checkGameOverCondition(ActorRef out) {
		if (this.getPlayer1().getHealth() <= 0 || this.getPlayer2().getHealth() <= 0) {
			String winner = this.getPlayer1().getHealth() <= 0 ? "Player 2 wins!" : "Player 1 Wins!";
			BasicCommands.addPlayer1Notification(out, winner, 5);
		}
		return;
	}
    
    public void endTurn () {
		if (currentPlayer == player1) {
			currentPlayer = player2;
		} else { 
			currentPlayer = player1;
		}
	}

    public ArrayList<Unit> getAllUnits() {
        ArrayList<Unit> allUnits = new ArrayList<>();
        
        // Assuming 'board' is the instance of Board within GameState
        if (this.board != null) {
            for (int x = 0; x < 9; x++) {  // Loop through board columns
                for (int y = 0; y < 5; y++) {  // Loop through board rows
                    Tile tile = this.board.getTile(x, y);
                    if (tile != null && tile.isHasUnit()) {
                        allUnits.add(tile.getUnit());
                    }
                }
            }
        }
        
        return allUnits;
	}

	public List<Tile> getValidWraithlingSwarmTiles() {
		// get valid wraithling swarm tiles
		List<Tile> validTiles = new ArrayList<Tile>();
		Board board = this.getBoard();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile t = board.getTile(i, j);
				boolean horizontalCheck = true, verticalCheck = true;
				
				// Check horizontally, but only if j-1 and j+1 are within bounds
				if (j > 0 && j < 4) {
					horizontalCheck = !board.getTile(i, j-1).isHasUnit() &&
									  !board.getTile(i, j).isHasUnit() &&
									  !board.getTile(i, j+1).isHasUnit();
				} else {
					horizontalCheck = false; // cannot satisfy the condition if on horizontal edge
				}
		
				// Check vertically, but only if i-1 and i+1 are within bounds
				if (i > 0 && i < 8) {
					verticalCheck = !board.getTile(i-1, j).isHasUnit() &&
									!board.getTile(i, j).isHasUnit() &&
									!board.getTile(i+1, j).isHasUnit();
				} else {
					verticalCheck = false; // cannot satisfy the condition if on vertical edge
				}
		
				// If either horizontal or vertical checks pass, add the tile
				if (horizontalCheck || verticalCheck) {
					validTiles.add(t);
				}
			}
		}
		
		return validTiles;
	}
}