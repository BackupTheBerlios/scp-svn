package de.scp.selector.ruleengine.rules.conditions;

public class Or extends AbstractBinaryOperator  {

	public Or(IFuzzyBoolComponent lhs, IFuzzyBoolComponent rhs) {
		super(lhs, rhs);
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate() {
		FuzzyBoolEnum lval = lhs.evaluate();
		FuzzyBoolEnum rval = rhs.evaluate();
		// shortcut one is true
		if (lval == FuzzyBoolEnum.TRUE || rval == FuzzyBoolEnum.TRUE)
			return FuzzyBoolEnum.TRUE;
		// both false
		if (lval == FuzzyBoolEnum.FALSE && rval == FuzzyBoolEnum.FALSE)
			return FuzzyBoolEnum.FALSE;
		return FuzzyBoolEnum.UNDEFINED;
	}

}
