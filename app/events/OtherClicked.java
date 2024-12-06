package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.Reset;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		// reset the lastClickedCard when clicking other space
		if (gameState.getLastClickedCard() != null) {
			// Turn all the tiles into transparent
			Reset.resetHighLightTiles(out, gameState);
			gameState.setLastClickedCard(null);
		}

		if (gameState.getLastClickedUnit() != null) {
			Reset.resetHighLightTiles(out, gameState);
			gameState.setLastClickedUnit(null);
		}

		// Reset.resetMovement(gameState);

	}
}


