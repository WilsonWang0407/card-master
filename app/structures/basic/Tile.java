package structures.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;

/**
 * A basic representation of a tile on the game board. Tiles have both a pixel
 * position
 * and a grid position. Tiles also have a width and height in pixels and a
 * series of urls
 * that point to the different renderable textures that a tile might have.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Tile {

	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
																// objects from a file
	private boolean hasUnit = false;
	private Unit unit;

	List<String> tileTextures;
	int xpos;
	int ypos;
	int width;
	int height;
	int tilex;
	int tiley;

	public Tile() {
	}

	public Tile(String tileTexture, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		tileTextures = new ArrayList<String>(1);
		tileTextures.add(tileTexture);
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}

	public Tile(List<String> tileTextures, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		this.tileTextures = tileTextures;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
	}

	public List<String> getTileTextures() {
		return tileTextures;
	}

	public void setTileTextures(List<String> tileTextures) {
		this.tileTextures = tileTextures;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getTilex() {
		return tilex;
	}

	public void setTilex(int tilex) {
		this.tilex = tilex;
	}

	public int getTiley() {
		return tiley;
	}

	public void setTiley(int tiley) {
		this.tiley = tiley;
	}

	public void setHasUnit(boolean hasUnit) {
		this.hasUnit = hasUnit;
	}

	public boolean isHasUnit() {
		return hasUnit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Unit getUnit() {
		return unit;
	}

	/**
	 * Loads a tile from a configuration file
	 * parameters.
	 * 
	 * @param configFile
	 * @return
	 */
	public static Tile constructTile(String configFile) {

		try {
			Tile tile = mapper.readValue(new File(configFile), Tile.class);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	// highlight valid tiles on the board (white)
	public void highlightValidTile(ActorRef out) {
		BasicCommands.drawTile(out, this, 1);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// highlight invalid tiles on the board (red)
	public void highlightInvalidTile(ActorRef out) {
		BasicCommands.drawTile(out, this, 2);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// return tiles to original state (transparent)
	public void resetTiles(ActorRef out) {
		BasicCommands.drawTile(out, this, 0);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void highlightValidMovementTiles(ActorRef out, Unit unit, Board board) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board.getTile(i, j);
				// Check if the unit can move to this tile (valid unit movement, within the
				// bounds of the board, and is not occupied)
				if (unit.isValidMovementRange(tile)
						&& unit.isValidBoardPosition(tile.getTilex(), tile.getTiley())
						&& !tile.hasUnit) {
					// Highlight the tile as valid (white)
					tile.highlightValidTile(out);
				}
			}
		}
	}

	public void highlightValidAttackTiles(ActorRef out, Unit unit, Board board) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board.getTile(i, j);

				Unit enemyUnit = tile.getUnit();
				int currentPlayerId = unit.getPlayerId();
				// check if this tile has an enemy unit and is within attack range
				if (enemyUnit != null && enemyUnit.getPlayerId() != currentPlayerId && unit.isValidAttackRange(tile)) {
					// highlight the tile as valid for attacking (red)
					tile.highlightInvalidTile(out);
				}

			}
		}
	}

	public void highlightValidSummonTile(ActorRef out) {
		BasicCommands.drawTile(out, this, 1);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean validSummonTile(GameState gameState, Tile clickedTile, Card clickedCard) {
		Player currentPlayer = gameState.player1sTurn ? gameState.player1 : gameState.player2;

		if (gameState.getCurrentPlayer() == currentPlayer && clickedCard.isCreature()) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					Tile tile = gameState.getBoard().getTile(i, j);
					Unit unit = tile.getUnit();
					if (unit != null && unit.getPlayerId() == gameState.getCurrentPlayer().getPlayerId()) {

						// Check if the clickedTile is within range
						for (int dx = -1; dx <= 1; dx++) {
							for (int dy = -1; dy <= 1; dy++) {
								int x = i + dx;
								int y = j + dy;

								if (x >= 0 && x < 9 && y >= 0 && y < 5) {
									Tile candidateTile = gameState.getBoard().getTile(x, y);

									// Valid if the candidateTile is the same as the clickedTile and is empty
									if (candidateTile == clickedTile && candidateTile.getUnit() == null) {
										return true; // Valid summoning location!
									}
								}
							}
						}
					}
				}
			}
		}
		return false; // Not a valid summoning location by default
	}

	public void summonWraithling(ActorRef out, GameState gameState) {
		Unit wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, gameState.unitId++, Unit.class);
		wraithling.setPositionByTile(this);
		wraithling.setPlayerId(2);
		wraithling.setAttack(1);
		wraithling.setHealth(1);
		wraithling.setMaxHealth(10);
		this.setUnit(wraithling);
		this.setHasUnit(true);
		// add the unit in the recording arrayList
		gameState.getPlayer1Units().add(wraithling);
		

		// frontend:
		// Load the effect animation for summoning
		EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		// Play the summoning effect animation on the target tile
		BasicCommands.playEffectAnimation(out, effect, this);
		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		// Draw the summoned unit on the target tile
		BasicCommands.drawUnit(out, wraithling, this);
		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());
		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}

		Abilities.openingGambit(out, gameState);
	}
}