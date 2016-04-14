package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import theory.BooleanAlgebra;

public abstract class LTLFormula<P,S> {

	public <E extends BooleanExpression> SAFA<P,S> getSAFA(BooleanAlgebra<P, S> ba){
		BooleanExpressionFactory<PositiveBooleanExpression> boolexpr = SAFA.getBooleanExpressionFactory();

		PositiveBooleanExpression initialState = boolexpr.MkState(0);
		HashMap<LTLFormula<P, S>, Integer> formulaToStateId = new HashMap<>();
		
		Collection<Integer> finalStates = new HashSet<>();
		HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves = new HashMap<>();
		
		this.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		Collection<SAFAInputMove<P, S>> transitions = new LinkedList<>();
		for(Collection<SAFAInputMove<P, S>> c: moves.values())
			transitions.addAll(c);		
		
		return SAFA.MkSAFA(transitions, initialState, finalStates, ba, false, true);
	}
	
	// Checks whether a formula should be a final state in the automaton
	public LTLFormula<P,S> pushNegations(BooleanAlgebra<P, S> ba){
		return pushNegations(true,ba);
	}
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract LTLFormula<P,S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba);	
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	protected abstract void accumulateSAFAStatesTransitions(
			HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates,
			BooleanAlgebra<P, S> ba);
	
	// returns set of disjoint predicates that are the triggers of transitions out of this state
	public abstract SAFA<P,S> getSAFANew(BooleanAlgebra<P, S> ba);
	
	// Checks whether a formula should be a final state in the automaton
	protected abstract boolean isFinalState();	
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		this.toString(sb);
		return sb.toString();
	}
	
	public abstract void toString(StringBuilder sb);
	
}