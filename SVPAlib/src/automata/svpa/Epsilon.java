/**
 * 
 */
package automata.svpa;

import java.util.Stack;



import theory.BooleanAlgebra;
import utilities.Pair;

public class Epsilon<U, S> extends SVPAMove<U, S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and
	 * ends at state <code>to</code> with input <code>input</code>
	 */
	public Epsilon(Integer from, Integer to) {
		super(from, to, Tag.Epsilon);
	}

	public boolean isDisjointFrom(SVPAMove<U, S> t, BooleanAlgebra<U, S> ba) {
		return t.from != from;
	}

	public boolean isSatisfiable(BooleanAlgebra<U, S> boolal) {
		return true;
	}

	public Pair<Integer, Stack<Pair<Integer, S>>> getNextState(
			Pair<Integer, Stack<Pair<Integer, S>>> state,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba) {
		return null;
	}

	@Override
	public String toString() {
		return String.format("E: %s --> %s", from, to);
	}

	@Override
	public Object clone() {
		return new Epsilon<U, S>(from, to);
	}

	@Override
    public int hashCode() {
		int hashZero = (from+1)*1+from;
		return (to+hashZero)*hashZero+to;
    }

    @Override
	public boolean equals(Object other) {
		if (other instanceof Epsilon<?, ?>) {
			Epsilon<?, ?> otherCasted = (Epsilon<?, ?>) other;
			return otherCasted.from == from && otherCasted.to == to;
		}

		return false;
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"&#949;\"]\n", from, to);
	}
	
	@Override
	public boolean isEpsilonTransition() {
		return true;
	}

	@Override
	public S getWitness(BooleanAlgebra<U, S> boolal) {
		// TODO Auto-generated method stub
		return null;
	}

}
