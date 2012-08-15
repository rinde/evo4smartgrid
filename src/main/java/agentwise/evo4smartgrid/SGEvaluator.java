/**
 * 
 */
package agentwise.evo4smartgrid;

import static java.util.Arrays.asList;

import java.util.Collection;

import rinde.cloud.javainterface.Computer;
import rinde.evo4mas.evo.gp.GPComputationResultImpl;
import rinde.evo4mas.evo.gp.GPEvaluator;
import rinde.evo4mas.evo.gp.GPProgram;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SGEvaluator extends GPEvaluator<SGCompJob, GPComputationResultImpl, SGContext> {

	private static final long serialVersionUID = 3803572332306084268L;
	// Scenario
	final double[] requestedEnergy = { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
	final int[] requestedTimes = { 6, 6, 6, 7, 7, 7, 9, 9, 10, 10 };
	final double maxPower = 3;
	final int cars = 10;
	final int tsim = 10;

	// Requested path
	final double[] requestedPath = { 0, 10, 20, 33, 33, 33, 53, 70, 82, 94, 100 };
	// Requested profile
	final double[] requestedProfile = { 10, 10, 13, 0, 0, 20, 17, 12, 12, 6 };

	@Override
	protected Collection<SGCompJob> createComputationJobs(GPProgram<SGContext> program) {
		// this would be the place where scenarios are loaded from disk,
		// currently just using one hard coded scenario
		return asList(new SGCompJob(program, new SmartGridScenario(requestedEnergy, requestedTimes, maxPower, cars,
				tsim, requestedPath, requestedProfile)));
	}

	@Override
	protected Computer<SGCompJob, GPComputationResultImpl> createComputer() {
		return new SGSimulator();
	}

	@Override
	protected int expectedNumberOfResultsPerGPIndividual() {
		return 1;
	}
}
