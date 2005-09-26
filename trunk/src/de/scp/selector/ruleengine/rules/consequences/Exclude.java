package de.scp.selector.ruleengine.rules.consequences;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.util.Logger;

public class Exclude implements IConsequence {
	private Enumeration attribute;
	private String[] excludes;

	public Exclude(AbstractAttribute attr, String[] excludes) {
		if (!(attr instanceof Enumeration)) {
			throw new RuntimeException("Exclude only applicable for Enumeration attributes");
		}
		// TODO check if values are in enumeration contained => else warning
		this.attribute = (Enumeration) attr;
		this.excludes = excludes;
	}

	public Result execute(int sequence) {
		return attribute.exclude(excludes, sequence);
	}

	@Override
	public String toString() {
		return "Exclude(" + attribute.getName() + "," + Logger.arrayToString(excludes) + ")";
	}

}
