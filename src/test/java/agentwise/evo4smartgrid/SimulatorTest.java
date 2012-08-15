/**
 * 
 */
package agentwise.evo4smartgrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rinde.evo4mas.evo.gp.GPProgramParser;
import agentwise.evo4smartgrid.original.SmartGridSimulation;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SimulatorTest {

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

	SmartGridScenario scenario = new SmartGridScenario(requestedEnergy, requestedTimes, maxPower, cars, tsim,
			requestedPath, requestedProfile);

	@Test
	public void test() {
		compareFitness("deltaE-deltaT^2-Pmin/Pmax", "(sub (sub deltae (pow deltat 2.0)) (div pmin pmax))");
		compareFitness("(Pmax+deltaE)", "(add pmax deltae)");
		compareFitness("(deltaT / Pmax)", "(div deltat pmax)");
		compareFitness("( (Pmax+deltaE) / (deltaT / Pmax) ) - ( (Pmin * Pmax)- (deltaT-X))", "(sub (div (add pmax deltae) (div deltat pmax)) (sub (mul pmin pmax) (sub deltat x)))");
	}

	public void compareFitness(String expr, String lisp) {
		final SmartGridSimulation sgs = new SmartGridSimulation();
		final SGSimulator sim = new SGSimulator();

		final SGCompJob job = new SGCompJob(GPProgramParser.parseProgram(lisp, new GPFunctions().create()), scenario);

		assertEquals(sgs.execute(expr), sim.compute(job).getFitness(), 0.0001);

	}
}
