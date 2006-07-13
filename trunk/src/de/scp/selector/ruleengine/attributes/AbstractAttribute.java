package de.scp.selector.ruleengine.attributes;

import java.util.ArrayList;
import java.util.List;

import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;
import de.scp.selector.util.PublicCloneable;

/**
 * Abstract implementation of an attribute.
 * 
 * @author Axel Sammet
 */
public abstract class AbstractAttribute {
	private String name;

	// TODO Check all changes on the sessioncontents for rollback

	private class AttributeContents implements PublicCloneable {
		private boolean assigned;
		private int sequence = Integer.MAX_VALUE;
		private List<String> violations = new ArrayList<String>();
		private PublicCloneable subcontents;

		public boolean isAssigned() {
			return assigned;
		}

		public void setAssigned(boolean assigned) {
			this.assigned = assigned;
		}

		public int getSequence() {
			return sequence;
		}

		public void setSequence(int sequence) {
			this.sequence = sequence;
		}

		public PublicCloneable getSubcontents() {
			return subcontents;
		}

		public void setSubcontents(PublicCloneable subcontents) {
			this.subcontents = subcontents;
		}

		public List<String> getViolations() {
			return violations;
		}

		public void setViolations(List<String> violations) {
			this.violations = violations;
		}

		@Override
		public PublicCloneable clone() {
			AttributeContents ac = new AttributeContents();
			ac.setAssigned(isAssigned());
			ac.setSequence(getSequence());
			if (getSubcontents() != null) {
				ac.setSubcontents(getSubcontents().clone());
			}
			return ac;
		}
	}

	public AbstractAttribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addViolation(SessionContents sc, String violation) {
		getAttributeContent(sc).getViolations().add(violation);
	}

	private AttributeContents getAttributeContent(SessionContents sc) {
		if (sc == null) {
			throw new RuntimeException("Missing SessionContents!");
		}
		AttributeContents cont = (AttributeContents) sc.getContents(getName());
		if (cont == null) {
			cont = new AttributeContents();
			sc.putContents(getName(), cont);
		}
		return cont;
	}

	protected PublicCloneable getContents(SessionContents sc) {
		return getAttributeContent(sc).getSubcontents();
	}

	protected void putContents(SessionContents sc, PublicCloneable contents) {
		getAttributeContent(sc).setSubcontents(contents);
	}

	public String[] getViolations(SessionContents sc) {
		return (String[]) getAttributeContent(sc).getViolations().toArray(
				new String[getAttributeContent(sc).getViolations().size()]);
	}
	
	public void removeViolations(SessionContents sc) {
		getAttributeContent(sc).getViolations().clear();
	}

	/**
	 * Returns the current value (currently selected value) of the attribute. The type of this
	 * depends on the type of the attribute!!!
	 * 
	 * @return
	 */
	public abstract Object getValue(SessionContents sc);

	public abstract String[] getSelectedValuesAsStrings(SessionContents sc);

	public abstract FuzzyBoolEnum equalsTo(SessionContents sc, Object str);

	public abstract IConsequence.Result assignEqual(SessionContents sc, Object obj, int sequence);

	public boolean isAssigned(SessionContents sc) {
		return getAttributeContent(sc).isAssigned();
	}

	public abstract IConsequence.Result select(SessionContents sc, String value, int sequence);

	/**
	 * @return Returns the sequence.
	 */
	public int getSequence(SessionContents sc) {
		return getAttributeContent(sc).getSequence();
	}

	/**
	 * @param sequence The sequence to set.
	 */
	public void setSequence(SessionContents sc, int sequence) {
		getAttributeContent(sc).setSequence(sequence);
	}

	/**
	 * Clears the attribute
	 */
	public abstract void clear(SessionContents session);

	public abstract Object[] getAllValues(SessionContents contents);

	protected void setAssigned(SessionContents sc, boolean assigned) {
		getAttributeContent(sc).setAssigned(assigned);
	}

}
