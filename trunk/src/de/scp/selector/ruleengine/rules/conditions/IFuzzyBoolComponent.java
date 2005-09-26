package de.scp.selector.ruleengine.rules.conditions;

import java.util.List;



/**
 * A FBC might be an expression or a composite of expressions which evaluates
 * to a FuzzyBoolEnum. Examples are Equals, And, Or ...
 * @author Axel Sammet
 */
public interface IFuzzyBoolComponent {

	public FuzzyBoolEnum evaluate();
	
	public List<IFuzzyBoolComponent> getSubComponents();
	
}
