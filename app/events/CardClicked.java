package events;


import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.MiniCard;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();
        
        // Get the current player
        Player currentPlayer = gameState.player1sTurn ? gameState.player1 : gameState.player2;
        
        // Get the card from the hand at the specified position
        int cardIndex = handPosition - 1; // Adjust position to match list index (0-based)
        if (gameState.getLastClickedUnit() != null) {
            Reset.resetHighLightTiles(out, gameState);
            gameState.setLastClickedUnit(null);
        }

        if(cardIndex >= 0 && cardIndex < currentPlayer.getHands().size()) {
            Card clickedCard = currentPlayer.getHands().get(cardIndex);

            if(clickedCard.getManacost() <= currentPlayer.getMana()){
                
                if(gameState.player1sTurn && clickedCard.isCreature()) {
                    // Find all Player 1 units and highlight tiles around them
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 5; j++) {
                            Tile tile = gameState.getBoard().getTile(i, j);
                            if (tile.getUnit() != null && tile.getUnit().getPlayerId() == 1) {
                                // Highlight valid tiles
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dy = -1; dy <= 1; dy++) {
                                        int x = i + dx;
                                        int y = j + dy;
                                        if (x >= 0 && x < 9 && y >= 0 && y < 5) {
                                            Tile targetTile = gameState.getBoard().getTile(x, y);
                                            targetTile.highlightValidSummonTile(out);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 5; j++) {
                            Tile tile = gameState.getBoard().getTile(i, j);
                            if (tile.getUnit() != null) {
                                BasicCommands.drawTile(out, tile, 0);
                                try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                            }
                        }
                    }
                }

                // click a card
                
                else if (gameState.player1sTurn && !clickedCard.isCreature()) {
                    if (clickedCard.getCardname().equals("Horn of the Forsaken")) {
                        // highlight the tile where avatar's standing on
                        currentPlayer.getAvatar().getTile(gameState).highlightValidTile(out);
                    } else if (clickedCard.getCardname().equals("Wraithling Swarm")) {
                        List<Tile> validTiles = gameState.getValidWraithlingSwarmTiles();
                        for (Tile t: validTiles) {
                            t.highlightValidTile(out);
                        }
                    } else if (clickedCard.getCardname().equals("Dark Terminus")) {
                        // highlight the enemy unit
                        for (Unit u: gameState.getPlayer2Units()) {
                            if (u != gameState.player2.getAvatar()) {
                                u.getTile(gameState).highlightValidTile(out);
                            }
                        }
                    }
                }
                gameState.setLastClickedCard(clickedCard);
            } 

            
        }
	}
}
