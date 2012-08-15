/**
 * 
 */
package agentwise.evo4smartgrid;

import ec.Evolve;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class EvoSmartGridMain {
	public static void main(String[] args) {
		Evolve.main(new String[] { "-file", "files/ec/smartgridgp.params" });

	}
}
