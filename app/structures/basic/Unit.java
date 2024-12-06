package structures.basic;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
																// objects from a file

	int id;
	int backendId = 0;
	int health;
	int attack;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	private int playerId;
	private boolean justSummoned = true;
	private boolean hasMoved = false;
	private boolean hasAttacked = false;
	private boolean isAvatar;
	private boolean isBeamshocked = false;
	private int maxHealth;
	private int artifact = 0;

	public Unit() {
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(0, 0, 0, 0);
		this.correction = correction;
		this.animations = animations;
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		this.position = new Position(currentTile.getXpos(), currentTile.getYpos(), currentTile.getTilex(),
				currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}

	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBackendId() {
		return backendId;
	}

	public void setBackendId(int backendId) {
		this.backendId = backendId;
	}

	public UnitAnimationType getAnimation() {
		return animation;
	}

	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	public void setBeamshocked(boolean isBeamshocked) {
		this.isBeamshocked = isBeamshocked;
	}

	public void checkBeamshocked() {
		if (this.isBeamshocked) {
			this.setHasMoved(true);
			this.setHasAttacked(true);
			this.isBeamshocked = false;
		}
	}

	public boolean isAvatar() {
		return isAvatar;
	}

	public void setAvatar(boolean avatar) {
		isAvatar = avatar;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int health) {
		this.maxHealth = health;
	}
	
	public boolean getHasAttacked() {
		return this.hasAttacked;
	}

	public void setHasAttacked(boolean hasAttacked) {
		this.hasAttacked = hasAttacked;	
	}

	public boolean getJustSummoned() {
		return this.justSummoned;
	}

	public void setJustSummoned(boolean justSummoned) {
		this.justSummoned = justSummoned;
	}

	public int getArtifact() {
		return this.artifact;
	}

	public void setArtifact(int artifact) {
		this.artifact = artifact;
	}

	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * 
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		if (tile != null) {
			this.position = new Position(tile.getXpos(), tile.getYpos(), tile.getTilex(), tile.getTiley());
		}
	}

	public Tile getTile(GameState gameState) {
		int x = this.getPosition().getTilex();
		int y = this.getPosition().getTiley();
		Board board = gameState.getBoard();
		return board.getTile(x, y);
	}

	// Ensure that the unit hasn't already moved this turn
	public boolean canMove() {
		return !this.getHasMoved();
	}

	// Ensure the unit stays within the board boundaries
	public boolean isValidBoardPosition(int x, int y) {
		return x >= 0 && x <= 8 && y >= 0 && y <= 4;
	}

	// Ensure that it is the turn of the unit's player
	public boolean canMoveThisTurn(int currentPlayerId) {
		return currentPlayerId == this.getPlayerId();
	}

	// Validate if a tile falls within the allowed movement range of a unit
	public boolean isValidMovementRange(Tile tile) {
		int currentX = this.getPosition().getXpos();
		int currentY = this.getPosition().getYpos();
		int targetX = tile.getXpos();
		int targetY = tile.getYpos();

		// Calculate the absolute differences in X and Y positions
		int deltaX = Math.abs(targetX - currentX) / 120;
		int deltaY = Math.abs(targetY - currentY) / 120;

		// Check if the movement is within the allowed range (2 tiles in any
		// cardinal direction or 1 tile diagonally)
		return ((deltaX + deltaY == 1) || (deltaX + deltaY == 2));
	}

	// to check if there's anything blocking the way to target tile
	public boolean isMovable(GameState gameState, Tile target) {
		if (target.isHasUnit())
			return false;

		Board board = gameState.getBoard();
		int x = this.getPosition().getTilex();
		int y = this.getPosition().getTiley();

		int targetX = target.getTilex();
		int targetY = target.getTiley();

		int deltaX = targetX - x;
		int deltaY = targetY - y;

		if (deltaX == 0 && deltaY == 0)
			return false;

		// right side
		if (deltaX > 0) {
			// right top
			if (deltaY < 0) {
				if (!board.getTile(x, y - 1).isHasUnit() || !board.getTile(x + 1, y).isHasUnit())
					return true;
			}
			// right
			else if (deltaY == 0) {
				if (!board.getTile(x + 1, y).isHasUnit())
					return true;
			}
			// right bottom
			else {
				if (!board.getTile(x + 1, y).isHasUnit() || !board.getTile(x, y + 1).isHasUnit())
					return true;
			}
		}

		// up or down
		else if (deltaX == 0) {
			// top
			if (deltaY < 0) {
				if (!board.getTile(x, y - 1).isHasUnit())
					return true;
			}
			// bottom
			else {
				if (!board.getTile(x, y + 1).isHasUnit())
					return true;
			}
		}

		// left side
		else {
			// left top
			if (deltaY < 0) {
				if (!board.getTile(x - 1, y - 1).isHasUnit() || !board.getTile(x - 1, y).isHasUnit())
					return true;
			}
			// left
			else if (deltaY == 0) {
				if (!board.getTile(x - 1, y).isHasUnit())
					return true;
			}
			// left bottom
			else {
				if (!board.getTile(x - 1, y).isHasUnit() || !board.getTile(x, y + 1).isHasUnit())
					return true;
			}
		}
		return false;
	}

	public List<Tile> getValidMovementTiles(GameState gameState) {
		List<Tile> validTiles = new ArrayList<Tile>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = gameState.board.getTile(i, j);
				if (isValidMovementRange(tile) && !tile.isHasUnit()) {
					if (isMovable(gameState, tile)) {
						validTiles.add(tile);
					}
				}
			}
		}
		return validTiles;
	}

	public List<Tile> getAttackableEnemyTiles(GameState gameState) {
		boolean isPlayer1 = this.getPlayerId() == 1;

		List<Tile> validTiles = this.getValidMovementTiles(gameState);
		List<Tile> validEnemyTiles = new ArrayList<Tile>();
		List<Unit> enemyUnits = isPlayer1 ? gameState.getPlayer2Units() : gameState.getPlayer1Units();
		
		// has moved
		// if (this.getHasMoved()) {
			
			List<Tile> surroundedTiles = this.getSurroundingTiles(gameState);
			for (Tile t : surroundedTiles) {
				if (t.isHasUnit()) {
					if (enemyUnits.contains(t.getUnit())) {
						validEnemyTiles.add(t);
					}
				}
			}
		// }

		// hasn't moved
		if (!this.getHasMoved()) {
			List<Tile> enemyTiles = new ArrayList<Tile>();
			// List<Tile> surroundedTiles = this.getSurroundingTiles(gameState);

			for (Unit e : enemyUnits) {
				int x = e.getPosition().getTilex(), y = e.getPosition().getTiley();
				Tile enemyTile = gameState.getBoard().getTile(x, y);
				enemyTiles.add(enemyTile);
			}

			for (Tile t : validTiles) {

				int tx = t.getTilex();
				int ty = t.getTiley();
				// System.out.println("T: " + tx + ", " + ty);

				for (Tile e : enemyTiles) {
					int ex = e.getTilex();
					int ey = e.getTiley();
					// System.out.println(" E: " + ex + ", " + ey);
					int deltaX = Math.abs(tx - ex);
					int deltaY = Math.abs(ty - ey);

					boolean valid = (deltaX + deltaY == 1 ||
							deltaX + deltaY == 2 &&
									deltaX < 2 &&
									deltaY < 2);

					if (valid) {
						validEnemyTiles.add(e);
					}
				}
			}
		}
		return validEnemyTiles;
	}

	public List<Tile> getSurroundingTiles(GameState gameState) {
		List<Tile> tiles = new ArrayList<Tile>();
		int tx = this.getPosition().getTilex();
		int ty = this.getPosition().getTiley();

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int x = tx + dx;
				int y = ty + dy;

				if (isValidBoardPosition(x, y)) {
					tiles.add(gameState.getBoard().getTile(x, y));
				}
			}
		}

		return tiles;
	}

	public void moveUnit(Tile tile, int currentPlayerId, ActorRef out, GameState gameState) {
		// Ensure it is the unit's player's turn
		if (this.canMoveThisTurn(currentPlayerId)) {
			// Ensure the unit hasn't already moved
			if (this.canMove()) {

				if (isValidMovementRange(tile) && isMovable(gameState, tile)) {
					if (isValidMovementRange(tile) && isMovable(gameState, tile)) {
						Tile origTile = this.getTile(gameState);
						// Mark the unit as moved
						this.setPositionByTile(tile);
						this.setHasMoved(true);
						tile.setHasUnit(true);
						tile.setUnit(this);
						origTile.setHasUnit(false);
						origTile.setUnit(null);
						BasicCommands.moveUnitToTile(out, this, tile);

						origTile.setHasUnit(false);
						origTile.setUnit(null);
						BasicCommands.moveUnitToTile(out, this, tile);

					} else {
						// Logic to send invalid move (out of movement range) message to frontend
						if (currentPlayerId == 1) {
							BasicCommands.addPlayer1Notification(out, "Invalid move. Move is outside of movement range.",
									3);
						} else {
							BasicCommands.addPlayer2Notification(out, "Invalid move. Move is outside of movement range.",
									3);
						}
					}
				} else {
					// Logic to send invalid move (unit has already moved) message to frontend
					if (currentPlayerId == 1) {
						BasicCommands.addPlayer1Notification(out, "Invalid move. This unit has already moved.", 	3);
					} else {
						BasicCommands.addPlayer2Notification(out, "Invalid move. This unit has already moved.", 3);
					}
				}
			} else {
				// Logic to handle invalid move (not this player's turn) message to frontend
				if (currentPlayerId == 1) {
					BasicCommands.addPlayer1Notification(out, "Invalid move. It is not your turn.", 3);
				} else {
					BasicCommands.addPlayer2Notification(out, "Invalid move. It is not your turn.", 3);
				}
			}
			Reset.resetHighLightTiles(out, gameState);
			gameState.setLastClickedUnit(null);
		}
	}

	public boolean isDead() {
		return this.health <= 0;
	}

	public boolean canAttack() {
		return !this.getHasAttacked();
	}

	public boolean isValidAttackRange(Tile tile) {
		int currentX = this.getPosition().getXpos();
		int currentY = this.getPosition().getYpos();
		int targetX = tile.getXpos();
		int targetY = tile.getYpos();

		// Calculate the absolute differences in X and Y positions
		int deltaX = Math.abs(targetX - currentX) / 120;
		int deltaY = Math.abs(targetY - currentY) / 120;

		// Check if the movement is within the allowed range (1 tile diagonally)
		return ((deltaX + deltaY == 1) || (deltaX + deltaY == 2) && (deltaX < 2) && (deltaY < 2));
	}

	public void attackUnit(Unit defendingUnit, int currentPlayerId, GameState gameState, ActorRef out) {
		// implement the logic of attacking another unit
		if (this.canMoveThisTurn(currentPlayerId)) {
			if (this.canAttack()) {
				Tile tile = defendingUnit.getTile(gameState);
				if (isValidAttackRange(tile) && tile.getUnit() != null) {
					// mark unit as attacked
					this.setHasAttacked(true);

					// check if it is an enemy unit
					if (this.getPlayerId() != defendingUnit.getPlayerId()) {
						// deal damage to target unit
						// backend:
						int damage = this.getAttack();
						int restHealth = (defendingUnit.getHealth() - damage > 0) ? defendingUnit.getHealth() - damage
								: 0;
						defendingUnit.setHealth(restHealth);

						// frontend:
						BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
						try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

						BasicCommands.playUnitAnimation(out, defendingUnit, UnitAnimationType.hit);
						try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

						BasicCommands.setUnitHealth(out, defendingUnit, defendingUnit.getHealth());
						try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

						Player player1 = gameState.getPlayer1();
						Player player2 = gameState.getPlayer2();

						if (defendingUnit == player1.getAvatar()) {
							player1.setHealth(defendingUnit.getHealth());
							BasicCommands.setPlayer1Health(out, gameState.getPlayer1());
							try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
							
							if (defendingUnit.getArtifact() > 0) {
								// summon a wraithling 
								List<Tile> surroundingTiles = defendingUnit.getSurroundingTiles(gameState);
								for (Tile t: surroundingTiles) {
									if (!t.isHasUnit()) {
										t.summonWraithling(out, gameState);
										break;
									}
								}
								defendingUnit.setArtifact(defendingUnit.getArtifact()-1);
							}
						} else if (defendingUnit == player2.getAvatar()) {
							player2.setHealth(defendingUnit.getHealth());
							BasicCommands.setPlayer2Health(out, gameState.getPlayer2());
							try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
						}

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);

						// check if target unit is dead
						if (defendingUnit.isDead()) {
							List<Unit> units = this.getPlayerId() == 1 ? gameState.getPlayer2Units() : gameState.getPlayer1Units();
							// backend:
							tile.setHasUnit(false);
							tile.setUnit(null);
							units.remove(defendingUnit);

							// frontend:
							BasicCommands.playUnitAnimation(out, defendingUnit, UnitAnimationType.death);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							Abilities.deathwatch(out, gameState);

							BasicCommands.deleteUnit(out, defendingUnit);
							
							gameState.checkGameOverCondition(out);

						}

						// if defending unit is not dead -> counter attack
						else {
							defendingUnit.counterAttack(this, gameState, out);

							// frontend:
							BasicCommands.playUnitAnimation(out, defendingUnit, UnitAnimationType.idle);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				if (currentPlayerId == 1) {
					BasicCommands.addPlayer1Notification(out, "Invalid move. This unit has already attacked.", 3);
				} else {
					BasicCommands.addPlayer2Notification(out, "Invalid move. This unit has already attacked.", 3);
				}
			}
		} else {
			if (currentPlayerId == 1) {
				BasicCommands.addPlayer1Notification(out, "Invalid move. It is not your turn.", 3);
			} else {
				BasicCommands.addPlayer2Notification(out, "Invalid move. It is not your turn.", 3);
			}
		}
		Reset.resetHighLightTiles(out, gameState);
		gameState.setLastClickedUnit(null);
	}

	public void counterAttack(Unit unit, GameState gameState, ActorRef out) {
		// deal damage to target unit
		// backend:
		int damage = this.getAttack();
		int restHealth = (unit.getHealth() - damage > 0) ? unit.getHealth() - damage : 0;
		unit.setHealth(restHealth);

		// frontend:
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);

		Player player1 = gameState.getPlayer1();
		Player player2 = gameState.getPlayer2();

		if (unit == player1.getAvatar()) {
			player1.setHealth(unit.getHealth());
			BasicCommands.setPlayer1Health(out, gameState.getPlayer1());
			try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

			if (unit.getArtifact() > 0) {
				System.out.println(123123123);

				// summon a wraithling 
				List<Tile> surroundingTiles = unit.getSurroundingTiles(gameState);
				for (Tile t: surroundingTiles) {
					if (!t.isHasUnit()) {
						t.summonWraithling(out, gameState);
						break;
					}
				}
			} 

		} else if (unit == player2.getAvatar()) {
			player2.setHealth(unit.getHealth());
			BasicCommands.setPlayer2Health(out, gameState.getPlayer2());
			try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
		}

		// check if target unit is dead
		if (unit.isDead()) {
			Tile tile = unit.getTile(gameState);
			List<Unit> units = this.getPlayerId() == 1 ? gameState.getPlayer1Units() : gameState.getPlayer2Units();
			// backend:
			tile.setHasUnit(false);
			tile.setUnit(null);
			units.remove(unit);

			// frontend:
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out, unit);
			Abilities.deathwatch(out, gameState);

			gameState.checkGameOverCondition(out);
		}
		
	}
	
}