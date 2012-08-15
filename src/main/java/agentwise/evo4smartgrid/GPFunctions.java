/**
 * 
 */
package agentwise.evo4smartgrid;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import rinde.evo4mas.evo.gp.GPFunc;
import rinde.evo4mas.evo.gp.GPFuncSet;
import rinde.evo4mas.evo.gp.GenericFunctions.Add;
import rinde.evo4mas.evo.gp.GenericFunctions.Constant;
import rinde.evo4mas.evo.gp.GenericFunctions.Div;
import rinde.evo4mas.evo.gp.GenericFunctions.If4;
import rinde.evo4mas.evo.gp.GenericFunctions.Mul;
import rinde.evo4mas.evo.gp.GenericFunctions.Pow;
import rinde.evo4mas.evo.gp.GenericFunctions.Sub;

public class GPFunctions extends GPFuncSet<SGContext> {

	private static final long serialVersionUID = -6704316556203618707L;

	@SuppressWarnings("unchecked")
	@Override
	public Collection<GPFunc<SGContext>> create() {
		return newArrayList(
		/* GENERIC FUNCTIONS */
		new If4<SGContext>(), /* */
				new Add<SGContext>(), /* */
				new Sub<SGContext>(), /* */
				new Div<SGContext>(), /* */
				new Mul<SGContext>(), /* */
				new Pow<SGContext>(), /* */
				/* CONSTANTS */
				new Constant<SGContext>(0), /* */
				new Constant<SGContext>(1), /* */
				new Constant<SGContext>(2), /* */
				/* DOMAIN SPECIFIC FUNCTIONS */
				new DeltaE(), new DeltaT(), new Pmin(), new Pmax(), new X()

		);
	}

	public static class DeltaE extends GPFunc<SGContext> {
		private static final long serialVersionUID = -4897612872245986056L;

		@Override
		public double execute(double[] input, SGContext context) {
			return context.deltaE;
		}
	}

	public static class DeltaT extends GPFunc<SGContext> {
		private static final long serialVersionUID = -8284508181428820325L;

		@Override
		public double execute(double[] input, SGContext context) {
			return context.deltaT;
		}
	}

	public static class Pmin extends GPFunc<SGContext> {
		private static final long serialVersionUID = 4727934406670091054L;

		@Override
		public double execute(double[] input, SGContext context) {
			return context.Pmin;
		}
	}

	public static class Pmax extends GPFunc<SGContext> {
		private static final long serialVersionUID = -4694467792729714763L;

		@Override
		public double execute(double[] input, SGContext context) {
			return context.Pmax;
		}
	}

	public static class X extends GPFunc<SGContext> {
		private static final long serialVersionUID = -3937926887201691877L;

		@Override
		public double execute(double[] input, SGContext context) {
			return context.x;
		}
	}
}
