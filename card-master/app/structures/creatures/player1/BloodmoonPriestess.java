package structures.creatures.player1;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;
import utils.StaticConfFiles;

/**
 * This is a representation of a Bloodmoon Priestess creature.
 * The creature card has a unique ID. The creature card has a 
 * set mana cost, attack, and health. Bloodmoon Priestess has the Deathwatch
 * ability. It moves and attacks according to the methods set in Unit. 
 *
 */

public class BloodmoonPriestess extends Unit{
    private int id;
    private int cost;
    private int attack;
    private int health;
	private boolean isCreature = true;
    private String config;
	private boolean hasOpeningGambit = true;
    
    public BloodmoonPriestess() {
    	this.id = 8;
    	this.cost = 4;
    	this.attack = 3;
    	this.health = 3;
    	this.config = StaticConfFiles.bloodmoonPriestess;
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
		BasicCommands.setUnitAttack(out, (Unit)this, attack);
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

	public boolean isCreature() {
		return isCreature;
	}

	public boolean getHasOpeningGambit() {
		return hasOpeningGambit;
	}

}