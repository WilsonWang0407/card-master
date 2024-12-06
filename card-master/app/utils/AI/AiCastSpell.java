package utils.AI;

import akka.actor.ActorRef;

import commands.BasicCommands;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.creatures.player2.IroncliffeGuardian;
import structures.creatures.player2.SaberspineTiger;
import structures.creatures.player2.SilverguardKnight;
import structures.creatures.player2.SilverguardSquire;
import structures.creatures.player2.SkyrockGolem;
import structures.creatures.player2.SwampEntangler;
import structures.creatures.player2.YoungFlamewing;
import structures.spells.BeamShock;
import structures.spells.SundropElixir;
import structures.spells.TrueStrike;
import utils.BasicObjectBuilders;
import utils.Reset;
import utils.StaticConfFiles;

public class AiCastSpell {
    public static void castSpell(ActorRef out, GameState gameState) {
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        Card toSummon = null;
		boolean done= false;
        while (!done) {
            List<Card> validCards = new ArrayList<Card>();
            for (Card card : player2.getHands()) {
                int maxManaCost = player2.getMana();
                
                if (card.getManacost() <= maxManaCost && !card.isCreature()) {
                    validCards.add(card);
                }
            }
            
            if (validCards.isEmpty()) done = true;
            
            toSummon = validCards.stream().max(Comparator.comparingInt(Card::getManacost)).orElse(null);
            if (toSummon == null) break;

            System.out.println(toSummon.getCardname());
            if(toSummon.getCardname().equals("Sundrop Elixir")){
                SundropElixir spell = new SundropElixir();
                spell.cast(player2, new Unit(), new Tile(), out, gameState);
            }
            else if(toSummon.getCardname().equals("Truestrike")){
                TrueStrike spell = new TrueStrike();
                spell.cast(player2, new Unit(), new Tile(), out, gameState);
            }
            else if(toSummon.getCardname().equals("Beamshock")){
                BeamShock spell = new BeamShock();
                spell.cast(player2, new Unit(), new Tile(), out, gameState);
            }
            int position = player2.getHands().indexOf(toSummon);
                    
            // Delete the card from the player's hand
            List<Card> hands = player2.getHands();
            hands.remove(toSummon);

            player2.useMana(toSummon.getManacost()); // Deduct mana cost
            BasicCommands.setPlayer2Mana(out, player2);
        }
		
        
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
