/**
 * 
 */
package automata.fsa;

import theory.BooleanAlgebra;

public class Epsilon<U,S> extends SFAMove<U,S> {

	/**
	 * Constructs an FSA Transition that starts from state <code>from</code> and ends at state
	 * <code>to</code> with input <code>input</code>
	 */
	public Epsilon(Integer from, Integer to) {
		super(from, to);
	}

	public boolean isDisjointFrom(SFAMove<U,S> t, BooleanAlgebra<U,S> ba){		
		return t.from!=from;
	}

	@Override
	public boolean isSatisfiable(BooleanAlgebra<U, S> boolal) {
		return true;
	}

	@Override
	public String toDotString() {
		return String.format("%s -> %s [label=\"&#949;\"]\n", from,to);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Epsilon<?, ?>) {
			Epsilon<?, ?> otherCasted = (Epsilon<?, ?>) other;
			return otherCasted.from==from && otherCasted.to==to;
		}

		return false;
	}
	
	@Override
	public String toString() {
		return String.format("E: %s --> %s", from, to);
	}
	
	@Override
	public Object clone(){
		  return new Epsilon<U, S>(from, to);
	}

	@Override
	public boolean isEpsilonTransition() {
		return true;
	}

	@Override
	public S getWitness(BooleanAlgebra<U, S> boolal) {
		return null;
	}

	@Override
	public boolean hasModel(S el, BooleanAlgebra<U, S> ba) {
		return false;
	}
	
}
