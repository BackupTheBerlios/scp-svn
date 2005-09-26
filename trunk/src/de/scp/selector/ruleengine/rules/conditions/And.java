package de.scp.selector.ruleengine.rules.conditions;



public class And extends AbstractBinaryOperator  {

	public And(IFuzzyBoolComponent lhs, IFuzzyBoolComponent rhs) {
		super(lhs, rhs);
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate() {
		FuzzyBoolEnum lval = lhs.evaluate();
		FuzzyBoolEnum rval = rhs.evaluate();
		// shortcut
		if (lval == FuzzyBoolEnum.FALSE || rval == FuzzyBoolEnum.FALSE)
			return FuzzyBoolEnum.FALSE;
		if (lval == FuzzyBoolEnum.TRUE && rval == FuzzyBoolEnum.TRUE)
			return FuzzyBoolEnum.TRUE;
		return FuzzyBoolEnum.UNDEFINED;
	}

}
