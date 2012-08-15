/**
 * 
 */
package agentwise.evo4smartgrid;

import static java.util.Arrays.asList;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
	private static final long serialVersionUID = 542274712463716913L;

	protected final SmartGridScenario scenario;

	public SGEvaluator() {
		try {
			scenario = SmartGridScenario.fromJson(new FileReader("files/scenarios/example.scen"));
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Collection<SGCompJob> createComputationJobs(GPProgram<SGContext> program) {
		return asList(new SGCompJob(program, scenario));
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
