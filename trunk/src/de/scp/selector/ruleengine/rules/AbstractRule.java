package de.scp.selector.ruleengine.rules;

import java.util.List;

import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;


/**
 * Abstract implementation of a rule.
 * 
 * @author Axel Sammet
 */
public abstract class AbstractRule {

	/**
	 * Result contains the following fields:<br>
	 * <li> <code>hasChangedAttributes</code>: one or more attributes have
	 * been set to a new value.</li>
	 * <li> <code>violationOccured</code>: during execution of the
	 * consequence a violation occured.</li>
	 * <li> <code>affectedAttributes</code>: all attributes that have been
	 * set as a consequence of this rule</li>
	 */
	public static class Result {
		private boolean hasFired;
		private IConsequence.Result result;

		public Result(IConsequence.Result result) {
			this.result = result;
		}

		/**
		 * @return Returns the affectedAttributes.
		 */
		public AbstractAttribute[] getAffectedAttributes() {
			return result.getAffectedAttributes();
		}

		/**
		 * @return Returns the hasChangedAttributes.
		 */
		public boolean hasChangedAttributes() {
			return result.hasChangedAttributes();
		}

		/**
		 * @return Returns the hasFired.
		 */
		public boolean hasFired() {
			return hasFired;
		}

		/**
		 * @param hasFired
		 *            Set the hasFired property.
		 */
		public void setHasFired(boolean hasFired) {
			this.hasFired = hasFired;
		}

		/**
		 * @return Returns the violationOccured.
		 */
		public boolean violationOccured() {
			return result.isViolated();
		}

		public String getViolation() {
			return result.getViolation();
		}
	}

	/**
	 * Evaluates if the the rule has to fire and propagates the resulting
	 * constraints to the affected attributes.
	 * 
	 * @return Returns an <code>AbstractRule.Result</code>
	 */
	public AbstractRule.Result execute(SessionContents sc, int sequence) {
		return internalExecute(sc, sequence);
	}

	/**
	 * Evaluates if the the rule has to fire and propagates the resulting
	 * constraints to the affected attributes.
	 * 
	 * @return Returns an <code>AbstractRule.Result</code>
	 */
	protected abstract AbstractRule.Result internalExecute(SessionContents sc, int sequence);

	/**
	 * @return Returns a List of all attributes the rule depends on. This
	 *         information is already needed before the actual execution of the
	 *         rule to build up the dependencies between attributes and rules.
	 */
	public abstract List<AbstractAttribute> dependencies();

}
