package de.scp.selector.ruleengine.rules.conditions;

import de.scp.selector.ruleengine.SessionContents;



public class And extends AbstractBinaryOperator  {

	public And(IFuzzyBoolComponent lhs, IFuzzyBoolComponent rhs) {
		super(lhs, rhs);
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate(SessionContents sc) {
		FuzzyBoolEnum lval = lhs.evaluate(sc);
		if (lval == FuzzyBoolEnum.FALSE)
			return FuzzyBoolEnum.FALSE;
		FuzzyBoolEnum rval = rhs.evaluate(sc);
		if (rval == FuzzyBoolEnum.FALSE)
			return FuzzyBoolEnum.FALSE;
		if (lval == FuzzyBoolEnum.TRUE && rval == FuzzyBoolEnum.TRUE)
			return FuzzyBoolEnum.TRUE;
		return FuzzyBoolEnum.UNDEFINED;
	}

}
