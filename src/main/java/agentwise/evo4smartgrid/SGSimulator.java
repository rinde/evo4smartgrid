/**
 * 
 */
package agentwise.evo4smartgrid;

import rinde.cloud.javainterface.Computer;
import rinde.evo4mas.evo.gp.GPComputationResultImpl;
import rinde.evo4mas.evo.gp.GPProgram;
import agentwise.evo4smartgrid.original.SmartGridSimulation;

/**
 * 
 * This implementation differs slightly from the one in
 * {@link SmartGridSimulation}. The most important one is that the division
 * operator in this implementation is protected (in the genetic programming
 * sense). This means that attempts to divide by 0 does not result in a
 * {@link java.lang.Double#NaN} but returns a predefined constant instead, see
 * {@link rinde.evo4mas.evo.gp.GenericFunctions.Div} for the definition.
 * 
 * @author Stijn Vandael
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SGSimulator implements Computer<SGCompJob, GPComputationResultImpl> {

	// in case these variables need to be varied, it is probably best to add
	// them to the scenario
	private static int overflowPenalty = 10;
	private static int underflowPenalty = 10;

	public GPComputationResultImpl compute(SGCompJob job) {
		final double fitness = execute(job.getProgram(), job.scenario);
		float floatFit;
		if (fitness == Double.POSITIVE_INFINITY || Double.isNaN(fitness) || fitness == Double.NEGATIVE_INFINITY) {
			floatFit = Float.MAX_VALUE;
		} else {
			floatFit = (float) fitness;
		}
		if (floatFit == Float.POSITIVE_INFINITY || floatFit == Float.NEGATIVE_INFINITY) {
			floatFit = Float.MAX_VALUE;
		}
		return new GPComputationResultImpl(job, floatFit);
	}

	private double execute(GPProgram<SGContext> heuristic, SmartGridScenario scenario) {
		// output parameters
		double penalty = 0;
		final double[] totalSolutionProfile = new double[scenario.tsim];
		final double[][] solutionProfiles = new double[scenario.cars][scenario.tsim];

		// simulation variables
		final double[] batteryEnergy = new double[scenario.cars];
		final double[] chargeEnergy = new double[scenario.cars];

		// simulation loop
		for (int t = 0; t < scenario.tsim; t++) {

			// 1. calculate bid function for every car
			final double[][] bidfunctions = new double[scenario.cars][101];
			for (int c = 0; c < scenario.cars; c++) {
				// if car is not departed and not fully charged
				if (t < scenario.requestedTimes[c] && batteryEnergy[c] < scenario.requestedEnergy[c]) {
					final double deltaE = scenario.requestedEnergy[c] - batteryEnergy[c];
					final int deltaT = scenario.requestedTimes[c] - t;

					// real maxPower taken into account battery level
					final double maxPower2 = Math.min(scenario.maxPower, deltaE);
					final double Pmin2 = Math.min(maxPower2, Math.max(deltaE - maxPower2 * (deltaT - 1), 0)); // min
																												// battery
					// final double[] bid = createBid(heuristic, deltaE, deltaT,
					// Pmin2, maxPower2);
					final double[] bid = createBid(heuristic, deltaE, deltaT, Pmin2, maxPower2);

					// store bid function
					bidfunctions[c] = bid;
				}
			}

			// 2. sum bid functions and determine priority
			final double[] totalbidfunction = new double[101];
			for (int c = 0; c < scenario.cars; c++) {
				if (t < scenario.requestedTimes[c] && batteryEnergy[c] < scenario.requestedEnergy[c]) {// if
					// car is not departed and not fully charged
					final double[] b = bidfunctions[c];
					for (int i = 0; i <= 100; i++) {
						totalbidfunction[i] = totalbidfunction[i] + b[i];
					}
				}
			}
			double priority = -1;
			for (int prio = 0; prio < 101; prio++) { // search equilibrium
				if (scenario.requestedProfile[t] >= totalbidfunction[prio]) {
					// interpolate
					if (prio != 0 && prio != 100 && totalbidfunction[t] != totalbidfunction[prio]) {
						priority = prio
								- ((scenario.requestedProfile[t] - totalbidfunction[prio]) / (totalbidfunction[prio - 1] - totalbidfunction[prio]));
					} else {
						priority = prio;
					}
					break;
				}
				priority = prio;
			}

			// 3. determine chargeEnergy for every car
			for (int c = 0; c < scenario.cars; c++) {
				if (t < scenario.requestedTimes[c] && batteryEnergy[c] < scenario.requestedEnergy[c]) {// if
					// car is not departed and not fully charged
					final int pfloor = (int) Math.floor(priority);
					final int pceil = (int) Math.ceil(priority);
					chargeEnergy[c] = bidfunctions[c][pfloor] - (bidfunctions[c][pfloor] - bidfunctions[c][pceil])
							* (priority - pfloor);
				}
			}

			// 4. calculate new battery state for each car
			for (int c = 0; c < scenario.cars; c++) {
				if (t < scenario.requestedTimes[c] && batteryEnergy[c] < scenario.requestedEnergy[c]) {// if
					// car is not departed and not fully charged store previous
					// battery level
					final double Eprev = batteryEnergy[c];
					// charge battery
					batteryEnergy[c] = batteryEnergy[c] + chargeEnergy[c];
					// check if battery overflow
					if (batteryEnergy[c] > scenario.requestedEnergy[c]) {
						// FIXME this looks like a bug? what should happen here?
						// throw new
						// IllegalArgumentException("battery overflow");
					}
					// charge power used
					solutionProfiles[c][t] = batteryEnergy[c] - Eprev;
				}
			}
		}

		// add penalties
		for (int c = 0; c < scenario.cars; c++) {
			if (batteryEnergy[c] > scenario.requestedEnergy[c]) {
				penalty = penalty + Math.pow(scenario.requestedEnergy[c] - batteryEnergy[c], overflowPenalty);
			}
			if (batteryEnergy[c] < scenario.requestedEnergy[c]) {
				penalty = penalty + Math.pow(scenario.requestedEnergy[c] - batteryEnergy[c], underflowPenalty);
			}
		}

		// sum all individual solution charge profiles
		for (final double[] s : solutionProfiles) {
			for (int t = 0; t < scenario.tsim; t++) {
				totalSolutionProfile[t] = totalSolutionProfile[t] + s[t];
			}
		}

		// calculate fitness
		double fitness = 0;
		for (int t = 0; t < scenario.tsim; t++) {
			fitness = fitness + Math.pow(totalSolutionProfile[t] - scenario.requestedProfile[t], 2);
		}

		return fitness;
	}

	private double[] createBid(GPProgram<SGContext> heuristic, double deltaE, double deltaT, double Pmin, double Pmax) {
		final double[] bid = new double[101];
		for (int x = 0; x <= 100; x++) {
			double y = heuristic.execute(new SGContext(deltaE, deltaT, Pmin, Pmax, x));
			if (y == Double.NaN) {
				y = 0;
			}
			bid[x] = y;
		}
		return bid;
	}

}
