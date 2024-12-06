package events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.BasicCommands;

import demo.Loaders_2024_Check;

import play.libs.Json;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { messageType = “initialize” }
 * 
 * @author Dr. Richard McCreadie
 *
 */

public class Initialize implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		gameState.gameInitialized = true;
		gameState.player1sTurn = true;

		gameState.board = new Board(out); // Initializes the board

		initializePlayers(gameState, out);
		
		createAvatar1(out, gameState);
		createAvatar2(out,gameState);
	}

	private void initializePlayers(GameState gameState, ActorRef out) {
		// create human player and init Deck and hand
		// gameState.player1 = new Player(20, 2);
		gameState.player1.initDeckForPlayer(1);
		gameState.player1.initHands(out, 1);
		gameState.player1.setTurnMana();
		
		BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BasicCommands.setPlayer1Health(out, gameState.getPlayer1());
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BasicCommands.addPlayer1Notification(out, "Player 1's Turn.", 2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BasicCommands.setPlayer2Health(out, gameState.getPlayer2());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// create ai player and init Deck and hand
		gameState.player2.initDeckForPlayer(2);
		gameState.player2.initHands(out, 2);

		// initialize the arrayList recording the units on the board
		gameState.player1Units = new ArrayList<Unit>();
		gameState.player2Units = new ArrayList<Unit>();
	}
	
		
	private static void createAvatar1(ActorRef out, GameState gameState) {
		
		Unit avatar1 = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, gameState.unitId++, Unit.class);
		Tile tileForPlayer1 = gameState.getBoard().getTile(1, 2);
		avatar1.setPosition(new Position(tileForPlayer1.getXpos(), tileForPlayer1.getYpos(), 1, 2));
		avatar1.setPlayerId(1);
		avatar1.setJustSummoned(false);
		avatar1.setAttack(2);
		avatar1.setHealth(20);
		gameState.getBoard().placeUnit(out, avatar1, 1, 2);
		gameState.getPlayer1().setAvatar(avatar1);
		gameState.player1Units.add(avatar1);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BasicCommands.setUnitHealth(out, avatar1, 20);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		BasicCommands.setUnitAttack(out, avatar1, 2);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void createAvatar2(ActorRef out, GameState gameState) {
		
		Unit avatar2 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.unitId++, Unit.class);
		Tile tileForPlayer2 = gameState.getBoard().getTile(7, 2);
		avatar2.setPosition(new Position(tileForPlayer2.getXpos(), tileForPlayer2.getYpos(), 7, 2));
		avatar2.setPlayerId(2);
		avatar2.setJustSummoned(false);
		avatar2.setAttack(2);
		avatar2.setHealth(20);
		gameState.getBoard().placeUnit(out, avatar2, 7, 2);
		gameState.getPlayer2().setAvatar(avatar2);
		gameState.player2Units.add(avatar2);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BasicCommands.setUnitAttack(out, avatar2, 2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, avatar2, 20);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

