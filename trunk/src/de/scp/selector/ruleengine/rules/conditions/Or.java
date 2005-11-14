package de.scp.selector.ruleengine.rules.conditions;

import de.scp.selector.ruleengine.SessionContents;

public class Or extends AbstractBinaryOperator {

	public Or(IFuzzyBoolComponent lhs, IFuzzyBoolComponent rhs) {
		super(lhs, rhs);
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate(SessionContents sc) {
		// first check shortcuts (one true => result true)
		FuzzyBoolEnum lval = lhs.evaluate(sc);
		if (lval == FuzzyBoolEnum.TRUE)
			return FuzzyBoolEnum.TRUE;
		FuzzyBoolEnum rval = rhs.evaluate(sc);
		if (rval == FuzzyBoolEnum.TRUE)
			return FuzzyBoolEnum.TRUE;
		// both false
		if (lval == FuzzyBoolEnum.FALSE && rval == FuzzyBoolEnum.FALSE)
			return FuzzyBoolEnum.FALSE;
		return FuzzyBoolEnum.UNDEFINED;
	}

}
