/**
 * 
 */
package agentwise.evo4smartgrid;

import rinde.evo4mas.evo.gp.GPComputationJobImpl;
import rinde.evo4mas.evo.gp.GPProgram;

/**
 * Smart grid computation job. All settings + info needed to perform an
 * experiment.
 * 
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SGCompJob extends GPComputationJobImpl<SGContext> {

	private static final long serialVersionUID = 3431928747960900344L;
	public final SmartGridScenario scenario;

	public SGCompJob(GPProgram<SGContext> prog, SmartGridScenario scen) {
		super("agentwise.evo4smartgrid.SGSimulator", prog);
		scenario = scen;
	}
}
