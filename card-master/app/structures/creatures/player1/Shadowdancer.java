package structures.creatures.player1;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;
import utils.StaticConfFiles;

/**
 * This is a representation of a Shadowdancer creature.
 * The creature card has a unique ID. The creature card has a 
 * set mana cost, attack, and health. Shadowdancer has the Deathwatch
 * ability. It moves and attacks according to the methods set in Unit. 
 *
 */

public class Shadowdancer extends Unit{
    private int id;
    private int cost;
    private int attack;
    private int health;
	private boolean isCreature = true;
    private String config;
	private boolean hasOpeningGambit = true;
    
    public Shadowdancer() {
    	this.id = 9;
    	this.cost = 5;
    	this.attack = 5;
    	this.health = 4;
    	this.config = StaticConfFiles.shadowdancer;
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