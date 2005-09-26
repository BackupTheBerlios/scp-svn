package de.scp.selector.ruleengine.rules;

import java.util.LinkedList;
import java.util.List;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;


public class Rule extends AbstractRule {
	IFuzzyBoolComponent condition;
	IConsequence consequence;

	public Rule(IFuzzyBoolComponent condition, IConsequence consequence) {
		this.consequence = consequence;
		this.condition = condition;
	}

	public FuzzyBoolEnum condition() {
		return condition.evaluate();
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.AbstractRule#dependencies()
	 */
	@Override
	public List<AbstractAttribute> dependencies() {
		return dependencies(condition);
	}

	/**
	 * @param condition2
	 * @return
	 */
	private List<AbstractAttribute> dependencies(IFuzzyBoolComponent comp) {
		List<AbstractAttribute> depList = new LinkedList<AbstractAttribute>();
		return null;
	}

	protected AbstractRule.Result internalExecute(int sequence) {
		AbstractRule.Result result = null;
		if (condition.evaluate().equals(FuzzyBoolEnum.TRUE)) {
			IConsequence.Result consresult = consequence.execute(sequence);
			result = new AbstractRule.Result(consresult);
			result.setHasFired(true);
			return result;
		} else {
			return new AbstractRule.Result(new IConsequence.Result());
		}
	}

	public String toString() {
		return condition + " => " + consequence;
	}

}
