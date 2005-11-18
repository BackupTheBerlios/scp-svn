package de.scp.selector.ruleengine;

import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.Rule;
import de.scp.selector.ruleengine.rules.conditions.And;
import de.scp.selector.ruleengine.rules.conditions.Equals;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent;
import de.scp.selector.ruleengine.rules.consequences.AssignEquals;
import junit.framework.TestCase;

/**
 * @author Axel Sammet
 */
public class RuleTests extends TestCase {

	public void testCondition() {
		Knowledgebase kb = new Knowledgebase();
		SessionContents sc = new SessionContents();
		Enumeration numbers1 = new Enumeration("numbers1", new String[] { "1", "2", "3", "4" });
		numbers1.select(sc, "3", 1);
		Enumeration numbers2 = new Enumeration("numbers2", new String[] { "1", "2", "3", "4" });
		IFuzzyBoolComponent compound = new And(new Equals(numbers1, "4"), new Equals(numbers2, "1"));
		// false and undefined => false
		assertTrue("shortcut for 'lhs is false' failed", compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
		compound = new And(new Equals(numbers2, "4"), new Equals(numbers1, "1"));
		assertTrue("shortcut for 'rhs is false' failed", compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
		numbers2.select(sc, "4", 1);
		assertTrue("evaluation 'true and false' failed", compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
		numbers1.select(sc, "1", 1);
		assertTrue("evaluation 'true and true' failed", compound.evaluate(sc) == FuzzyBoolEnum.TRUE);
		numbers2 = new Enumeration("numbers", new String[] { "1", "2", "3", "4" });
		compound = new And(new Equals(numbers2, "4"), new Equals(numbers1, "1"));
		assertTrue("evaluation of undefined expression failed",
				compound.evaluate(sc) == FuzzyBoolEnum.UNDEFINED);
	}

	public void testConsequence() {
		Knowledgebase kb = new Knowledgebase();
		SessionContents sc = new SessionContents();
		Enumeration numbers1 = new Enumeration("numbers1", new String[] { "1", "2", "3", "4" });
		numbers1.select(sc, "4", 1);
		Enumeration numbers2 = new Enumeration("numbers2", new String[] { "1", "2", "3", "4" });
		numbers2.select(sc, "1", 1);
		Enumeration numbers3 = new Enumeration("numbers2", new String[] { "1", "2", "3", "4" });
		Rule rule = new Rule(new And(new Equals(numbers1, "4"), new Equals(numbers2, "1")),
				new AssignEquals(numbers3, "3"));
		assertTrue("Execution should return true", rule.execute(sc, 1).hasFired());
		assertEquals(numbers3, "3");
	}

	public void testPerformance() {
		int count = 1000;
		Knowledgebase kb = new Knowledgebase();
		for (int i = 0; i < count; i++) {
			kb.createAttribute(new Enumeration("enum" + i, new String[] { "a", "b", "c", "d" }));
		}
		for (int i = count-1; i > 0; i--) {
			kb.createRule(new Rule(new Equals(kb.getAttribute("enum"+(i-1)), "a"), new AssignEquals(
					kb.getAttribute("enum"+(i)), new String[] { "a" })));
		}
		Session session = new Session(kb);
		session.setAttribute("enum0","a");
	}

}
