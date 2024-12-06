package utils.AI;

import akka.actor.ActorRef;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import utils.Reset;

public class AiEndTurn {
    public static void endturn(ActorRef out, GameState gameState) {
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();
        player2.clearMana();
        BasicCommands.setPlayer2Mana(out, player2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

        player2.nextTurn();
		player2.drawCardsInHand(out, 2);
		
		BasicCommands.setPlayer1Mana(out, player1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.addPlayer1Notification(out, "Player 1's Turn.", 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		
		gameState.player1sTurn = true;
		
        Reset.resetAttackAndMovement(gameState);
    }
}
