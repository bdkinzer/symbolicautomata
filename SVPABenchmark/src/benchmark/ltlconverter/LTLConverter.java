package benchmark.ltlconverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;

import LTLparser.AlwaysNode;
import LTLparser.AndNode;
import LTLparser.EquivalenceNode;
import LTLparser.EventuallyNode;
import LTLparser.FalseNode;
import LTLparser.FormulaNode;
import LTLparser.IdNode;
import LTLparser.ImplicationNode;
import LTLparser.NegationNode;
import LTLparser.NextNode;
import LTLparser.OrNode;
import LTLparser.ReleaseNode;
import LTLparser.StrongReleaseNode;
import LTLparser.TrueNode;
import LTLparser.UntilNode;
import LTLparser.WeakUntilNode;
import LTLparser.XorNode;
import logic.ltl.And;
import logic.ltl.Eventually;
import logic.ltl.False;
import logic.ltl.Globally;
import logic.ltl.LTLFormula;
import logic.ltl.Next;
import logic.ltl.Not;
import logic.ltl.Or;
import logic.ltl.Predicate;
import logic.ltl.True;
import logic.ltl.Until;
import logic.ltl.WeakUntil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import utilities.Pair;

public class LTLConverter {

	public static Pair<BDDSolver, LTLFormula<BDD, BDD>> getLTLBDD(FormulaNode phi) {
		Set<String> atoms = phi.returnLeafNodes();
		HashMap<String, Integer> atomToInt = new HashMap<String, Integer>();
		for (String atom : atoms)
			atomToInt.put(atom, atomToInt.size());
		BDDSolver bdds = new BDDSolver(atomToInt.size());
		return new Pair<BDDSolver, LTLFormula<BDD, BDD>>(bdds,
				getLTLBDD(phi, atomToInt, bdds, new HashMap<String, LTLFormula<BDD, BDD>>()));
	}

	public static LTLFormula<BDD, BDD> getLTLBDD(FormulaNode phi, HashMap<String, Integer> atomToInt, BDDSolver bdds,
			HashMap<String, LTLFormula<BDD, BDD>> formulas) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		phi.unparse(pw, 0);
		String s = sw.toString();
		if (formulas.containsKey(s)) {
			return formulas.get(s);
		} else {

			LTLFormula<BDD, BDD> outputLTL = null;

			if (phi instanceof AlwaysNode) {
				AlwaysNode cphi = (AlwaysNode) phi;
				LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);

				outputLTL = new Globally<BDD, BDD>(subphi);
			} else {
				if (phi instanceof AndNode) {
					AndNode cphi = (AndNode) phi;
					LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);
					LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds, formulas);

					outputLTL = new And<BDD, BDD>(left, right);
				} else {
					if (phi instanceof EquivalenceNode) {
						EquivalenceNode cphi = (EquivalenceNode) phi;
						LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);
						LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds, formulas);

						outputLTL = new And<BDD, BDD>(new Or<BDD, BDD>(new Not<BDD, BDD>(left), right),
								new Or<BDD, BDD>(right, new Not<BDD, BDD>(left)));
					} else {
						if (phi instanceof EventuallyNode) {
							EventuallyNode cphi = (EventuallyNode) phi;
							LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds, formulas);

							outputLTL = new Eventually<BDD, BDD>(subphi);
						} else {
							if (phi instanceof FalseNode) {
								return new False<BDD, BDD>();
							} else {
								if (phi instanceof IdNode) {
									IdNode cphi = (IdNode) phi;
									outputLTL = new Predicate<BDD, BDD>(
											bdds.factory.ithVar(atomToInt.get(cphi.getName())));
								} else {
									if (phi instanceof ImplicationNode) {
										ImplicationNode cphi = (ImplicationNode) phi;
										LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds,
												formulas);
										LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt, bdds,
												formulas);

										outputLTL = new Or<BDD, BDD>(new Not<>(left), right);
									} else {
										if (phi instanceof NegationNode) {
											NegationNode cphi = (NegationNode) phi;
											LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt, bdds,
													formulas);
											outputLTL = new Not<BDD, BDD>(subphi);
										} else {
											if (phi instanceof NextNode) {
												NextNode cphi = (NextNode) phi;
												LTLFormula<BDD, BDD> subphi = getLTLBDD(cphi.getMyLTL1(), atomToInt,
														bdds, formulas);
												outputLTL = new Next<BDD, BDD>(subphi);
											} else {
												if (phi instanceof OrNode) {
													OrNode cphi = (OrNode) phi;
													LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(), atomToInt,
															bdds, formulas);
													LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(), atomToInt,
															bdds, formulas);

													outputLTL = new Or<BDD, BDD>(left, right);
												} else {
													if (phi instanceof ReleaseNode) {
														ReleaseNode cphi = (ReleaseNode) phi;
														LTLFormula<BDD, BDD> left = getLTLBDD(cphi.getMyLTL1(),
																atomToInt, bdds, formulas);
														LTLFormula<BDD, BDD> right = getLTLBDD(cphi.getMyLTL2(),
																atomToInt, bdds, formulas);

														outputLTL = new Not<BDD, BDD>(
																new Until<>(new Not<>(left), new Next<>(right)));
													} else {
														if (phi instanceof StrongReleaseNode) {
															// StrongReleaseNode
															// cphi =
															// (StrongReleaseNode)
															// phi;
															throw new NotImplementedException();
														} else {
															if (phi instanceof TrueNode) {
																outputLTL = new True<BDD, BDD>();
															} else {
																if (phi instanceof UntilNode) {
																	UntilNode cphi = (UntilNode) phi;
																	LTLFormula<BDD, BDD> left = getLTLBDD(
																			cphi.getMyLTL1(), atomToInt, bdds,
																			formulas);
																	LTLFormula<BDD, BDD> right = getLTLBDD(
																			cphi.getMyLTL2(), atomToInt, bdds,
																			formulas);

																	outputLTL = new Until<BDD, BDD>(left, right);
																} else {
																	if (phi instanceof WeakUntilNode) {
																		WeakUntilNode cphi = (WeakUntilNode) phi;
																		LTLFormula<BDD, BDD> left = getLTLBDD(
																				cphi.getMyLTL1(), atomToInt, bdds,
																				formulas);
																		LTLFormula<BDD, BDD> right = getLTLBDD(
																				cphi.getMyLTL2(), atomToInt, bdds,
																				formulas);

																		outputLTL = new WeakUntil<BDD, BDD>(left,
																				right);
																	} else {
																		if (phi instanceof XorNode) {
																			XorNode cphi = (XorNode) phi;

																			LTLFormula<BDD, BDD> left = getLTLBDD(
																					cphi.getMyLTL1(), atomToInt, bdds,
																					formulas);
																			LTLFormula<BDD, BDD> right = getLTLBDD(
																					cphi.getMyLTL2(), atomToInt, bdds,
																					formulas);

																			outputLTL = new Or<BDD, BDD>(
																					new And<BDD, BDD>(
																							new Not<BDD, BDD>(left),
																							right),
																					new And<BDD, BDD>(right,
																							new Not<BDD, BDD>(left)));
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}

						}
					}
				}
			}
			formulas.put(s, outputLTL);
			return outputLTL;
		}
	}
	
	
	public static void toMona(FormulaNode phi, String fileName) throws IOException {
		
		//throws IO
		//try {
		//	FileReader inFile = new FileReader(fileName);
		//} catch (FileNotFoundException ex) {
		//	System.err.println("File " + fileName + " not found.");
		//	System.exit(-1);
		//}
		Set<String> atoms = phi.returnLeafNodes();
		HashMap<String, Integer> atomToInt = new HashMap<String, Integer>();
		for (String atom : atoms){
			atomToInt.put(atom, atomToInt.size());
		}
			
		// gets string
		StringBuilder sb = new StringBuilder();
		//Preamble
		sb.append("WS1S; \n");
		// add all declarations of propositions
		//e.g. var2 A1;
		for (String atom : atoms){
			int count = atomToInt.get(atom);
			sb.append("var2 A"+ count +";\n");
		}
		// add all predicates
		//e.g. ex1 p1: p1 in A1;
		for (String atom : atoms){
			int count = atomToInt.get(atom);
			sb.append("ex1 p"+ count +": p" + count +" in A" + count +";\n");
		}
		
		// calls below
		toMona(phi, atomToInt, sb, 0, 1);
		sb.append(";\n");
		
		//dumps in file
		BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(fileName+".mona")));
		
		//write contents of StringBuffer to a file
        bwr.write(sb.toString());
        //flush the stream
        bwr.flush();
        //close the stream
        bwr.close();
	}
	
	
	//Skeleton method
	
	public static void toMona(FormulaNode phi, HashMap<String, Integer> atomToInt, StringBuilder sb, int var, int varcount) {
		String oldVar = "x"+var;
		String newVar1 = "x"+varcount;
		String newVar2 = "x"+(varcount+1);
		
		if (phi instanceof AlwaysNode) {
			//all y, x<=y<=last -> toMona(child, y)
			AlwaysNode cphi = (AlwaysNode) phi;
			sb.append("(all1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) => ");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+1);
			
			
		} else if (phi instanceof AndNode){
			//toMona(left) && toMona(right)
			AndNode cphi = (AndNode) phi;
			sb.append("(");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" & ");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof EquivalenceNode) {
			//toMona(left) <=> toMona(right)
			EquivalenceNode cphi = (EquivalenceNode) phi;
			sb.append("(");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" <=> ");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof EventuallyNode) {
			//exists y, x<=y<=last && toMona(child, y)
			EventuallyNode cphi = (EventuallyNode) phi;
			sb.append("(ex1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) & ");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+1);
			
			
		} else if (phi instanceof FalseNode) {
			sb.append("(false)");
			
		} else if (phi instanceof IdNode) {
			//find which proposition it represents, then print it
			//e.g. p1; (declared in preamble) 
			IdNode cphi = (IdNode) phi;
			sb.append("(p"+atomToInt.get(cphi.getName())+")");
		} else if (phi instanceof ImplicationNode) {
			//toMona(left) => toMona(right)
			ImplicationNode cphi = (ImplicationNode) phi;
			sb.append("( ");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" => ");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof NegationNode) {
			// toMona(~child)=  ~toMona(child)
			NegationNode cphi = (NegationNode) phi;
			sb.append("(~");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof NextNode) {
			// exists y, y=x+1 && toMona(child, y) 
			NextNode cphi = (NextNode) phi;
			sb.append("(");
			sb.append("(ex1 "+ newVar1 + ": " + newVar1 + " = " + oldVar +"+1 " + ") &");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof OrNode) {
			//toMona(left) || toMona(right)
			OrNode cphi = (OrNode) phi;
			sb.append("(");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" | ");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
			
		} else if (phi instanceof ReleaseNode) {
			//a0 R a1 = ~(~a0 U ~a1)
			ReleaseNode cphi = (ReleaseNode) phi;
			sb.append("(~(");
			
			sb.append("(ex1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) & ~");
			toMona(cphi.getMyLTL2(), atomToInt, sb, varcount, varcount+1);
			sb.append(" & ");
			sb.append("( all1 "+ newVar2 + ": " + oldVar + "<=" + newVar2 + "<=last) => ~");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+2);
			
			sb.append("))");
			
		} else if (phi instanceof StrongReleaseNode) {
			//not yet implemented
			
			
		} else if (phi instanceof TrueNode) {
			sb.append("(true)");
			
		} else if (phi instanceof UntilNode) {
			// exists y, x<=y<=last && toMona(right, y) && all z, x<=z<=y -> toMona(left, z)
			UntilNode cphi = (UntilNode) phi;
			sb.append("(");
			
			sb.append("(ex1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) & ");
			toMona(cphi.getMyLTL2(), atomToInt, sb, varcount, varcount+1);
			sb.append(" & ");
			sb.append("(all1 "+ oldVar + ": " + newVar2 + "<=" + newVar1 + "<=last) => ");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+2);
			
			sb.append(")");
			

		} else if (phi instanceof WeakUntilNode) {
			//a0 W a1 = a0 U a1 || G(a0) 
			WeakUntilNode cphi = (WeakUntilNode) phi;
			sb.append("(");
			
			sb.append("(ex1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) & ");
			toMona(cphi.getMyLTL2(), atomToInt, sb, varcount, varcount+1);
			sb.append(" & ");
			sb.append("(all1 "+ oldVar + ": " + newVar2 + "<=" + newVar1 + "<=last) => ");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+2);
			sb.append(" | ");
			sb.append("(all1 "+ newVar1 + ": " + oldVar + "<=" + newVar1 + "<=last) => ");
			toMona(cphi.getMyLTL1(), atomToInt, sb, varcount, varcount+1);
			
			sb.append(" ) ");
			
		} else if (phi instanceof XorNode) {
			// (x & ~y) | (~x & y)
			XorNode cphi = (XorNode) phi;
			sb.append("(");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" & ~");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(" | ~");
			toMona(cphi.getMyLTL1(),atomToInt, sb, varcount, varcount+1);
			sb.append(" & ");
			toMona(cphi.getMyLTL2(),atomToInt, sb, varcount, varcount+1);
			sb.append(")");
		} else{
			System.err.println("Wrong instance of phi, program will quit");
			System.exit(-1);
		}
		
	}

}
