package structures.creatures.player1;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;
import utils.StaticConfFiles;

/**
 * This is a representation of a Rock Pulveriser creature.
 * The creature card has a unique ID. The creature card has a 
 * set mana cost, attack, and health. Rock Pulveriser has the 
 * Provoke ability. It moves and attacks according to the methods 
 * set in Unit. 
 *
 */

public class RockPulveriser extends Unit {
    int id;
    int cost;
    int attack;
    int health;
    String config;
    
    public RockPulveriser() {
    	this.id = 5;
    	this.cost = 2;
    	this.attack = 1;
    	this.health = 4;
    	this.config = StaticConfFiles.rockPulveriser;
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
}