package de.scp.selector.ruleengine.rules.consequences;

import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.util.Logger;

public class AssignEquals implements IConsequence {
	AbstractAttribute attribute;

	Object value;

	public AssignEquals(AbstractAttribute attr, Object value) {
		this.attribute = attr;
		this.value = value;
	}

	public AssignEquals(Enumeration attr, Object[] values) {
		this.attribute = attr;
		this.value = values;
	}

	/**
	 * @see de.scp.selector.ruleengine.rules.consequences.IConsequence#execute()
	 */
	public IConsequence.Result execute(SessionContents sc, int sequence) {
		return attribute.assignEqual(sc, value, sequence);
	}

	@Override
	public String toString() {
		String valstr = (value instanceof Object[]) ?
			Logger.arrayToString((Object[]) value):
			"'"+value.toString()+"'";
		return "AssignEquals("+attribute.getName() + ", " + valstr + ")";
	}
}
