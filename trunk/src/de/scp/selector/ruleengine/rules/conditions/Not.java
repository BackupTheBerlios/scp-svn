package de.scp.selector.ruleengine.rules.conditions;

import java.util.LinkedList;
import java.util.List;

public class Not implements IFuzzyBoolComponent {
	IFuzzyBoolComponent comp;

	public Not(IFuzzyBoolComponent comp) {
		this.comp = comp;
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#evaluate()
	 */
	public FuzzyBoolEnum evaluate() {
		FuzzyBoolEnum res = comp.evaluate();
		if (res.equals(FuzzyBoolEnum.FALSE))
			return FuzzyBoolEnum.TRUE;
		if (res.equals(FuzzyBoolEnum.TRUE))
			return FuzzyBoolEnum.FALSE;
		return FuzzyBoolEnum.UNDEFINED;
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent#getSubComponents()
	 */
	public List<IFuzzyBoolComponent> getSubComponents() {
		List<IFuzzyBoolComponent> ret = new LinkedList<IFuzzyBoolComponent>();
		ret.add(comp);
		return ret;
	}

	public String toString() {
		return "Not(" + comp + ")";
	}

}
