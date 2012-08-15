/**
 * 
 */
package agentwise.evo4smartgrid;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import scala.actors.threadpool.Arrays;

import com.google.gson.Gson;

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

	@Override
	public boolean equals(Object other) {
		if (other instanceof SmartGridScenario) {
			final SmartGridScenario sgs = (SmartGridScenario) other;
			return Arrays.equals(requestedEnergy, sgs.requestedEnergy)
					&& Arrays.equals(requestedTimes, sgs.requestedTimes) //
					&& maxPower == sgs.maxPower //
					&& cars == sgs.cars //
					&& tsim == sgs.tsim //
					&& Arrays.equals(requestedPath, sgs.requestedPath)
					&& Arrays.equals(requestedProfile, sgs.requestedProfile);
		}
		return false;
	}

	public static SmartGridScenario fromJson(String json) {
		return new Gson().fromJson(json, SmartGridScenario.class);
	}

	public static SmartGridScenario fromJson(Reader reader) {
		return new Gson().fromJson(reader, SmartGridScenario.class);
	}

	public static String toJson(SmartGridScenario scenario) {
		return new Gson().toJson(scenario);
	}

	public static void toJson(SmartGridScenario scenario, Writer writer) throws IOException {
		new Gson().toJson(scenario, writer);
		writer.close();
	}

}
