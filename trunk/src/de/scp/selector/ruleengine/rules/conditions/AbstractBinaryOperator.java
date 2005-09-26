package de.scp.selector.ruleengine.rules.conditions;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractBinaryOperator implements IFuzzyBoolComponent {

	protected IFuzzyBoolComponent lhs;
	protected IFuzzyBoolComponent rhs;

	public AbstractBinaryOperator() {
	}
	
	public AbstractBinaryOperator(IFuzzyBoolComponent lhs, IFuzzyBoolComponent rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#getSubComponents()
	 */
	public List<IFuzzyBoolComponent> getSubComponents() {
		List<IFuzzyBoolComponent> components = new LinkedList<IFuzzyBoolComponent>();
		if (lhs!=null) components.add(lhs);
		if (rhs!=null) components.add(rhs);
		return new LinkedList<IFuzzyBoolComponent>();
	}

}
