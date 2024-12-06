package structures.basic;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

public class Board {
    private Tile[][] tiles;
    

    public Board(ActorRef out) {
        tiles = new Tile[9][5];  //board has 5 xs and 9 ys
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 5; j++) {
                tiles[i][j] = BasicObjectBuilders.loadTile(i, j);
                BasicCommands.drawTile(out, tiles[i][j], 0);
            	try {
        			Thread.sleep(1);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
            }
        }
    }

    public void placeUnit(ActorRef out, Unit unit, int x, int y) {
        Tile tile = getTile (x, y);
        if(tile != null && !tile.isHasUnit()) {
            tile.setHasUnit(true);
            tile.setUnit(unit);
            BasicCommands.drawUnit(out,  unit,  tile);
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            BasicCommands.setUnitAttack(out, unit, unit.getAttack());
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            BasicCommands.setUnitHealth(out, unit, unit.getHealth());
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
    	} else {
    		BasicCommands.addPlayer1Notification(out, "Invalid tile", 2);
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    } 

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 5;
    }
}
