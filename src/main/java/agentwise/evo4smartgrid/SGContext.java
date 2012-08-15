package agentwise.evo4smartgrid;

/**
 * 
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class SGContext {

	public final double deltaE;
	public final double deltaT;
	public final double Pmin;
	public final double Pmax;
	public final int x;

	public SGContext(double de, double dt, double pn, double px, int xx) {
		deltaE = de;
		deltaT = dt;
		Pmin = pn;
		Pmax = px;
		x = xx;
	}

}