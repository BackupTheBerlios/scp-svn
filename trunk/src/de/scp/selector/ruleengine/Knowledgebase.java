package de.scp.selector.ruleengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.rules.AbstractRule;
import de.scp.selector.util.Logger;

/**
 * A Knowledbase contains a set of attribute- and rule defintions. Therefore it
 * provides methods to create attributes and rules. Furthermore it implements
 * package visible methods for a Session to manipulate its contents in
 * accordance with the rules, i.e. the values of the attributes in that session.
 * 
 * @see de.scp.selector.ruleengine.SessionContents
 * @author Axel Sammet
 */
public class Knowledgebase {
	private Map<String, AbstractAttribute> allAttributesMap = new LinkedHashMap<String, AbstractAttribute>();
	private List<AbstractRule> allRules = new ArrayList<AbstractRule>();

	public void createAttribute(AbstractAttribute attr) {
		Logger.getInstance().info("Adding attribute: " + attr);
		if (allAttributesMap.containsKey(attr.getName()))
			throw new RuntimeException("Created duplicate attribute");
		allAttributesMap.put(attr.getName(), attr);
	}

	public void createRule(AbstractRule rule) {
		Logger.getInstance().info("Adding rule: " + rule);
		allRules.add(rule);
	}

	public AbstractAttribute getAttribute(String name) {
		return allAttributesMap.get(name);
	}

	void setAttribute(SessionContents sc, String name, String value) {
		Logger.getInstance().info("Setting attribute " + name + " = " + value);
		AbstractAttribute attr = getAttribute(name);
		// for everey NEW input we generate a higher number
		// rules from lower sequences dominate later inputs
		int attrSequence = attr.getSequence();
		int currentInputSequence = sc.getNextSequenceId();
		if (attrSequence > currentInputSequence) {
			attrSequence = currentInputSequence;
		}
		attr.select(sc, value, 1);
		// TODO rule conflicts do not result in errors at the moment, if they
		// occur during one selection
		// (they overwrite each other instead)
		fireRules(sc, attr, attrSequence);
		attr.setSequence(attrSequence);
	}

	private void fireRules(SessionContents sc, AbstractAttribute attr, int sequenceId) {
		int noOfRulesChecked = 0;
		int noOfRulesFired = 0;
		long time0 = System.currentTimeMillis();
		boolean notFinished;
		clearViolations(sc);
		List<AbstractRule> rules = new ArrayList<AbstractRule>(allRules);
		do {
			notFinished = false;
			Iterator<AbstractRule> it = rules.iterator();
			while (it.hasNext()) {
				AbstractRule rule = it.next();
				AbstractRule.Result result = rule.execute(sc, sequenceId);
				noOfRulesChecked++;
				if (result.violationOccured()) {
					Logger.getInstance().debug("Violation occured: " + result.getViolation());
					attr.addViolation(result.getViolation());
				}
				if (result.hasChangedAttributes()) {
					// knowledge base changed => we are not finished yet
					noOfRulesFired++;
					notFinished = true;
					Logger.getInstance().debug("Fired rule: " + rule);
					// remove rules when fired
					if (result.hasFired()) {
						it.remove();
					}
				}
			}
		} while (notFinished);
		Logger.getInstance().info(
				"(" + noOfRulesChecked + " / " + noOfRulesFired + ") (checked / fired) rules in "
						+ (System.currentTimeMillis() - time0) + " msecs");
	}

	/**
	 * Clears violations for all attributes.
	 */
	private void clearViolations(SessionContents sc) {
		for (AbstractAttribute att : getAllAttributes()) {
			att.removeViolations(sc);
		}
	}

	/**
	 * @return Returns the allAttributes.
	 */
	public Collection<AbstractAttribute> getAllAttributes() {
		return allAttributesMap.values();
	}

	/**
	 * Deletes the contents for all attributes.
	 */
	void clear(SessionContents session) {
		for (AbstractAttribute attrib : allAttributesMap.values()) {
			attrib.clear(session);
		}
	}

}
