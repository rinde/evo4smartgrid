package agentwise.evo4smartgrid.original;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;


public class SmartGridSimulation implements SmartGridSimInterface{
	
	public static void main(String[] args) {
		SmartGridSimulation sim = new SmartGridSimulation();
		double fitness = sim.execute("deltaE-deltaT^2-Pmin/Pmax");
		System.out.println(fitness);
	}
	
	/**
	 * FITNESS
	 * chi^2 of requested path
	 * 
	 * PENALTY
	 * battery overflow and  underflow 
	 * penalty = (E_flow)^penaltyparam
	 * 
	 */
	int overflowPenalty = 10;
	int underflowPenalty = 10;
	
	
	//Scenario
	final double[] requestedEnergy = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	final int[] requestedTimes = {6, 6, 6, 7, 7, 7, 9, 9, 10, 10};
	final double maxPower = 3;
	final int cars = 10;
	final int tsim = 10;
	
	//Requested path
	final double[] requestedPath = {0, 10, 20, 33, 33, 33, 53, 70, 82, 94, 100};
	//Requested profile
	final double[] requestedProfile = {10, 10, 13, 0, 0, 20, 17, 12, 12, 6};
	
	/**
	 * Execute Smart Grid algorithm with particular heuristic
	 * 
	 */
	public double execute(String heuristic) {
		//output parameters
		double penalty = 0;
		double[] totalSolutionProfile = new double[tsim];
		double[][] solutionProfiles = new double[cars][tsim];
		
		//simulation variables
		double[] batteryEnergy = new double[cars];
		double[] chargeEnergy = new double[cars];
		
		//simulation loop
		for(int t = 0; t < tsim; t++) {
			
			//1. calculate bid function for every car
			double[][] bidfunctions = new double[cars][101];
			for(int c = 0; c < cars; c++) {
				if(t < requestedTimes[c] && batteryEnergy[c] < requestedEnergy[c]){//if car is not departed and not fully charged
					double deltaE = requestedEnergy[c] - batteryEnergy[c];
					int deltaT = requestedTimes[c] - t;
					double maxPower2 = Math.min(maxPower, deltaE); //real maxPower taken into account battery level
					double Pmin2 = Math.min(maxPower2, Math.max(deltaE - maxPower2 * (deltaT-1),0)); //min power to fully charge battery
					double[] bid = createBid(heuristic, deltaE, deltaT, Pmin2, maxPower2);
			        //store bid function
					bidfunctions[c] = bid;
				}
				
			}
			
			//2. sum bid functions and determine priority
		    double[] totalbidfunction = new double[101];
		    for(int c = 0; c < cars; c++) {
		    	if(t < requestedTimes[c] && batteryEnergy[c] < requestedEnergy[c]){//if car is not departed and not fully charged
		    		double[] b = bidfunctions[c];
			    	for(int i = 0; i <= 100; i++) {
			    			totalbidfunction[i] = totalbidfunction[i] + b[i]; 
			    	} 
		    	}
		    }
		    double priority = -1;
		    for(int prio = 0; prio < 101; prio++) { //search equilibrium
		    	if(requestedProfile[t] >= totalbidfunction[prio]) {
		    		//interpolate
		            if(prio != 0 && prio != 100 && totalbidfunction[t] != totalbidfunction[prio]) {
		            	priority = prio - ((requestedProfile[t] - totalbidfunction[prio])/(totalbidfunction[prio-1] - totalbidfunction[prio]));
		            }else{
		            	priority = prio;
		            }
		            break;
		    	}
		    	priority = prio;
		    }
		    
		    //3. determine chargeEnergy for every car
		    for(int c = 0; c < cars; c++) {
		    	if(t < requestedTimes[c] && batteryEnergy[c] < requestedEnergy[c]){//if car is not departed and not fully charged
		    		int pfloor = (int)Math.floor(priority);
		    		int pceil = (int)Math.ceil(priority);
		    		chargeEnergy[c] = bidfunctions[c][pfloor] - (bidfunctions[c][pfloor] - bidfunctions[c][pceil])*(priority-pfloor);
		    	}
		    }
		        
		    //4. calculate new battery state for each car
		    for(int c = 0; c < cars; c++) {
		    	if(t < requestedTimes[c] && batteryEnergy[c] < requestedEnergy[c]){//if car is not departed and not fully charged
		    		//store previous battery level
		    		double Eprev = batteryEnergy[c];
			        //charge battery
		    		batteryEnergy[c] = batteryEnergy[c] + chargeEnergy[c];
		    		//check if battery overflow
			     	if(batteryEnergy[c] > requestedEnergy[c]) {
			     		//throw new IllegalArgumentException("battery overflow");
			     	}
			     	//charge power used
			     	solutionProfiles[c][t] = batteryEnergy[c] - Eprev;
		    	}
		    }
		}
		
		//add penalties
		for(int c = 0; c < cars; c++) {
			if(batteryEnergy[c] > requestedEnergy[c]){
				penalty = penalty + Math.pow(requestedEnergy[c]-batteryEnergy[c],overflowPenalty);
			}
			if(batteryEnergy[c] < requestedEnergy[c]) {
				penalty = penalty + Math.pow(requestedEnergy[c]-batteryEnergy[c],underflowPenalty);
			}
		}
		
		//sum all individual solution charge profiles
	    for(double[] s: solutionProfiles) {
	    	for(int t = 0; t < tsim; t++) {
	    		totalSolutionProfile[t] = totalSolutionProfile[t] + s[t]; 
	    	} 
	    }

	    //calculate fitness
	    double fitness = 0;
    	for(int t = 0; t < tsim; t++) {
    		fitness = fitness + Math.pow(totalSolutionProfile[t] - requestedProfile[t], 2); 
    	} 
	    
	    
	    return fitness;
	}
	
	private double[] createBid(String heuristic, double deltaE, double deltaT, double Pmin, double Pmax) {
		double[] bid = new double[101];
		for(int x = 0; x <=100; x++) {
			//parse heuristic
			Calculable calc = null;
			try {
				calc = new ExpressionBuilder(heuristic)
				.withVariable("deltaE", deltaE) //determines shape
				.withVariable("deltaT", deltaT)	//determines shape
				.withVariable("Pmin", Pmin)		//determines shape
				.withVariable("Pmax", Pmax)		//determines shape
				.withVariable("X", x)		//determines shape
				.build();
			} catch (UnknownFunctionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnparsableExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double y = calc.calculate();
			if(y ==  Double.NaN) {
				y = 0;
			}
			bid[x] = y;
		}
		return bid;
	}
	
}
