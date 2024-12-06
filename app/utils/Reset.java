package utils;

import actors.GameActor;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import akka.actor.ActorRef; // Import the missing package

public class Reset {

    Reset() {

    }

    public static void resetHighLightTiles(ActorRef out, GameState gameState) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tile = gameState.board.getTile(i, j);
                BasicCommands.drawTile(out, tile, 0);
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void resetAttackAndMovement(GameState gameState) {
		for (Unit u: gameState.getPlayer1Units()) {
			System.out.println(u.getHasMoved());
			u.setHasMoved(false);
            u.setHasAttacked(false);
            u.setJustSummoned(false);
		}

		for (Unit u: gameState.getPlayer2Units()) {
			u.setHasMoved(false);
            u.setHasAttacked(false);
            u.setJustSummoned(false);
		}
        gameState.setLastClickedCard(null);
        gameState.setLastClickedUnit(null);
	}
}
