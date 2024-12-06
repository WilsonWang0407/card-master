package structures.interfaces;

import structures.basic.Unit;

/*
 * Cards with Provoke:
 * Rock Pulveriser (Abyssian Swarm)
 * Silverguard Knight (Lyonar Generalist)
 * Ironcliffe Guardian (Lyonar Generalist)
 */

public interface Provoke {
    public void summon();

    public void provoke(Unit targetUnit);

    public void terminateProvoke();
}