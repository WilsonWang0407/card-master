package structures.creatures.player2;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.Reset;
import utils.StaticConfFiles;

/**
 * This is a representation of a Silverguard Squire creature.
 * The creature card has a unique ID. The creature card has a 
 * set mana cost, attack, and health. Silverguard Squire has the 
 * Opening Gambit ability. It moves and attacks according to the 
 * methods set in Unit. 
 *
 */

public class SilverguardSquire extends Unit {
    private int id;
    private int cost;
    private int attack;
    private int health;
    private boolean isCreature = true;
    private String config;
    private int maxHealth;

    public SilverguardSquire() {
        this.id = 11;
        this.cost = 1;
        this.attack = 1;
        this.health = 1;
        this.maxHealth = 1;
        this.config = StaticConfFiles.silverguardSquire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(ActorRef out, int attack) {
        this.attack = attack;
        BasicCommands.setUnitAttack(out, (Unit) this, attack);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(ActorRef out, int health) {
        this.health = health;
        BasicCommands.setUnitHealth(out, this, health);
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int newHealth) {
        this.maxHealth = newHealth;
    }

    // This unit can move to any unoccupied space on the board (flying ability)
    @Override
    public void moveUnit(Tile tile, int currentPlayerId, ActorRef out, GameState gameState) {

        // Iterate through the tiles on the board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                Tile currentTile = gameState.getBoard().getTile(i, j);

                // If the tile doesn't have a unit, highlight it as valid
                if (!currentTile.isHasUnit()) {
                    currentTile.highlightValidTile(out);
                }
            }
        }

        // Player selects valid tile
        Tile clickedTile = gameState.getLastClickedTile();

        // Ensure the clicked tile is not null
        if (clickedTile != null) {
            // Check if the clicked tile is unoccupied
            if (!clickedTile.isHasUnit()) {
                // Update unit's position to the clicked tile
                setPositionByTile(clickedTile);

                // Mark the unit as moved
                setHasMoved(true);
            } else {
                // Handle the case where the clicked tile is occupied
                if (currentPlayerId == 1) {
                    BasicCommands.addPlayer1Notification(out, "Cannot move to an occupied tile.", 3);
                } else {
                    BasicCommands.addPlayer2Notification(out, "Cannot move to an occupied tile.", 3);
                }
            }
        }

        // Clear highlighted tiles after the move
        Reset.resetHighLightTiles(out, gameState);
    }

}