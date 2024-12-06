package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import commands.BasicCommands;
import structures.basic.Unit;
import utils.Reset;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case the end-turn button.
 * 
 * { messageType = “endTurnClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		Player player1 = gameState.getPlayer1();
		Player player2 = gameState.getPlayer2();

		player1.clearMana();
		BasicCommands.setPlayer1Mana(out, player1);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player1.nextTurn();
		player1.drawCardsInHand(out, 1);

		BasicCommands.setPlayer2Mana(out, player2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		BasicCommands.addPlayer2Notification(out, "Player 2's Turn.", 2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		gameState.player1sTurn = false;


		// clean up the last turn hasMoved
		Reset.resetAttackAndMovement(gameState);
		Reset.resetHighLightTiles(out, gameState);

		// gameState.player2CastSpell(out, gameState);
		gameState.player2Summon(out, gameState);
		// gameState.player2MoveAndAttack(out, gameState);
		gameState.player2EndTurn(out, gameState);

	}
}
