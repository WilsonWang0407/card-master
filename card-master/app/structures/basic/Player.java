package structures.basic;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;
import utils.OrderedCardLoader;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 *
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int playerId;
	int health;
	int mana;
	public Unit avatar;
	List<Card> hands;
	List<Card> deck;
	public static final int MAX_MANA = 9;
	public int turnNumber;

	public Player(int playerId) {
		super();
		this.playerId = playerId;
		this.health = 20;
		this.mana = 0;

		this.hands = new ArrayList<Card>(6);
		this.deck = new ArrayList<Card>(20);
		this.avatar = new Unit();
		this.turnNumber = 1;
		this.avatar.setAvatar(true);
	}

	public Player(int health, int mana) {
	    super();
	    this.health = health;
        this.mana = mana;

        this.hands = new ArrayList<Card>(6);
        this.deck = new ArrayList<Card>(20);
        this.avatar = new Unit();
        this.turnNumber = 1;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public int getturnNumber() {
		return turnNumber;

	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	public Unit getAvatar() {
		return this.avatar;
	}
	public void setAvatar(Unit avatar) {
		this.avatar = avatar;
	}
	public List<Card> getDeck() {
		return this.deck;
	}
	public List<Card> getHands() {
		return this.hands;
	}
	public void nextTurn() {
        turnNumber++;
        setTurnMana();
        setHealth(health);
    }
	public void clearMana() {
		setMana(0);
	}
	public void setTurnMana() {

        int calculatedMana = 1 + turnNumber;

        if (calculatedMana > MAX_MANA) {
            setMana(MAX_MANA);
        } else {
            setMana(calculatedMana);
        }
    }

	public void initDeckForPlayer(int playerId) {
		if (playerId == 1) {
			this.deck = OrderedCardLoader.getPlayer1Cards(10);
		} else {
			this.deck = OrderedCardLoader.getPlayer2Cards(10);
		}
	}

	// draw 3 cards to human player hand
	public void initHands(ActorRef out, int playerId) {
		if (playerId == 1) {
			for (int i = 0; i < 3; i++) {
				try {
					Card card = this.deck.remove(0);
					this.hands.add(card);
					this.displayCard(out, card);
				} catch ( IndexOutOfBoundsException e) {
					System.out.println("Not enough cards in deck to draw initial hand");
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				try {
					Card card = this.deck.remove(0);
					this.hands.add(card);
				} catch ( IndexOutOfBoundsException e) {
					System.out.println("Not enough cards in deck to draw initial hand");
				}
			}
		}

	}

	// draw 2 cards to player hand
	public void drawCardsInHand(ActorRef out, int playerId) {
		if (playerId == 1) {
			if (this.getHands().size() >= 6) return; //hand is full
			try {
				Card card = this.deck.remove(0);
				this.hands.add(card);
				this.displayCard(out, card);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Not enough cards in deck to draw");
			}
		} else {
			if (this.getHands().size() >= 6) return; //hand is full
			try {
				Card card = this.deck.remove(0);
				this.hands.add(card);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Not enough cards in deck to draw");
			}
		}		
	}
	// display current hands
	public void displayCard(ActorRef out, Card card) {

		// draw the cards
		int position = this.hands.indexOf(card) + 1;
		if (card.getManacost() <= this.mana) {
			BasicCommands.drawCard(out, card, position, 1);
		} else {
			BasicCommands.drawCard(out, card, position, 0);
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		public void incrementMana(int amountToAdd) {
			this.mana = Math.min(this.mana + amountToAdd, MAX_MANA);
		}

		public boolean useMana (int cost) {
			if (this.mana >= cost) {
				this.mana -= cost;
				return true;
			}
			return false;
		}
}
