/**
 * 
 */
package agentwise.evo4smartgrid;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SmartGridScenario {

	// Scenario
	public final double[] requestedEnergy;
	public final int[] requestedTimes;
	public final double maxPower;
	public final int cars;
	public final int tsim;

	// Requested path
	public final double[] requestedPath;
	// Requested profile
	public final double[] requestedProfile;

	public SmartGridScenario(double[] reqEnergy, int[] reqTimes, double maxPow, int car, int ts, double[] reqPath,
			double[] reqProf) {
		requestedEnergy = reqEnergy;
		requestedTimes = reqTimes;
		maxPower = maxPow;
		cars = car;
		tsim = ts;
		requestedPath = reqPath;
		requestedProfile = reqProf;
	}

}
