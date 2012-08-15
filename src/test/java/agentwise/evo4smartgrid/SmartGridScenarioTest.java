/**
 * 
 */
package agentwise.evo4smartgrid;

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SmartGridScenarioTest {

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
	public void jsonTest() throws IOException {
		final String json1 = SmartGridScenario.toJson(scenario);
		final SmartGridScenario scen = SmartGridScenario.fromJson(json1);
		assertEquals(scenario, scen);

		final String json2 = SmartGridScenario.toJson(scen);
		assertEquals(json1, json2);

		SmartGridScenario.toJson(scenario, new FileWriter("files/scenarios/example.scen"));
	}
}
