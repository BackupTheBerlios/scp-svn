package de.scp.selector.ruleengine.rules.consequences;

import java.util.LinkedList;
import java.util.List;

import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;


public interface IConsequence {
	/**
	 * Result contains the following fields:<br>
	 * <li> <code>hasFired</code>: rule has fired as the conditions evaluated
	 * as true</li>
	 * <li> <code>hasChangedAttributes</code>: one or more attributes have
	 * been set to a new value.</li>
	 */
	public static class Result {
		private boolean hasChangedAttributes;
		private String violation;
		private List<AbstractAttribute> affectedAttributes;

		/**
		 * @return Returns true if the consequence violates the current values.
		 */
		public boolean isViolated() {
			if (violation != null)
				return true;
			return false;
		}

		public Result() {
			affectedAttributes = new LinkedList<AbstractAttribute>();
		}

		public void merge(Result res) {
			if (res.isViolated()) {
				this.violation = res.getViolation();
			}
			affectedAttributes.addAll(res.affectedAttributes);
			hasChangedAttributes |= res.hasChangedAttributes;
		}

		/**
		 * @return Returns the affectedAttributes.
		 */
		public AbstractAttribute[] getAffectedAttributes() {
			return (AbstractAttribute[]) affectedAttributes
					.toArray(new AbstractAttribute[affectedAttributes.size()]);
		}

		/**
		 * Adds an attribute to the list of affected / changed attributes. An
		 * attributes is called affected, if it was set to a value.
		 * 
		 * @param affectedAttribute
		 */
		public void addAffectedAttribute(AbstractAttribute affectedAttribute) {
			affectedAttributes.add(affectedAttribute);
		}

		/**
		 * @return Returns true if this consequence affected any attributes
		 *         (meaning they were set to a value). This does not include
		 *         attributes, which where constrainted by the exclusion of
		 *         values in an attribute.
		 */
		public boolean hasChangedAttributes() {
			return affectedAttributes.size() > 0;
		}

		/**
		 * @return Returns the violation.
		 */
		public String getViolation() {
			return violation;
		}

		/**
		 * @param violation
		 *            The violation to set.
		 */
		public void setViolation(String violation) {
			this.violation = violation;
		}
	}

	/**
	 * Executes the consequence.
	 * 
	 * @return
	 */
	public Result execute(SessionContents sc, int sequence);

}
