package structures.creatures.player2;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Unit;
import structures.interfaces.Provoke;
import utils.StaticConfFiles;

/**
 * This is a representation of an Ironcliffe Guardian creature.
 * The creature card has a unique ID. The creature card has a 
 * set mana cost, attack, and health. Ironcliffe Guardian has the 
 * Provoke ability. It moves and attacks according to the methods 
 * set in Unit. 
 *
 */

public class IroncliffeGuardian extends Unit implements Provoke {
    private int id;
    private int cost;
    private int attack;
    private int health;
    private boolean isCreature = true;
    private String config;
    private int maxHealth;

    public IroncliffeGuardian() {
        this.id = 16;
        this.cost = 5;
        this.attack = 3;
        this.health = 10;
        this.maxHealth = 10;
        this.config = StaticConfFiles.ironcliffeGuardian;
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

    @Override
    public void summon() {
    }

    // Enemy units in adjacent squares cannot move and can only attack this creature or other creatures with Provoke
    @Override
    public void provoke(Unit targetUnit) {
    }

    @Override
    public void terminateProvoke() {
    }
}