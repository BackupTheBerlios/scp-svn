package de.scp.selector.ruleengine.rules.conditions;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;

public class Equals extends AbstractBinaryOperator {
	private AbstractAttribute lhs;
	private Object rhs;

	public Equals(AbstractAttribute lhs, Object rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Equals(Object rhs, AbstractAttribute lhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate() {
		return (lhs.equalsTo(rhs));
	}

	public String toString() {
		return "Equals(" + lhs.getName() + "," + rhs + ")";
	}

}
