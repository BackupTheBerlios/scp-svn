package de.scp.selector.ruleengine.attributes;

import java.util.ArrayList;
import java.util.List;

import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;

/**
 * Abstract implementation of an attribute.
 * @author Axel Sammet
 */
public abstract class AbstractAttribute {
	boolean assigned;
	private String name;
	private List<String> violations = new ArrayList<String>();
	private int sequence;

	public AbstractAttribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return equalsTo(obj).equals(FuzzyBoolEnum.TRUE);
	}
	
	public void addViolation(String violation) {
		violations.add(violation);
	}
	
	public String[] getViolations() {
		return (String[]) violations.toArray(new String[violations.size()]);
	}
	
	public void removeViolations() {
		violations.clear();
	}
	
	/**
	 * Returns the current value (currently selected value) of the attribute.
	 * The type of this depends on the type of the attribute!!!
	 * @return
	 */
	public abstract Object getValue();

	public abstract FuzzyBoolEnum equalsTo(Object str);

	public abstract IConsequence.Result assignEqual(Object obj, int sequence);

	public boolean isAssigned() {
		return assigned;
	}

	/**
	 * @param value
	 */
	public abstract boolean select(String value, int sequence);

	/**
	 * @return Returns the sequence.
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence The sequence to set.
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * Clears the attribute
	 */
	public abstract void clear(Session session);
	
}
