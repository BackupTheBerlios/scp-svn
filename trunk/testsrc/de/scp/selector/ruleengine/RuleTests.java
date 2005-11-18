package de.scp.selector.ruleengine;

import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.Rule;
import de.scp.selector.ruleengine.rules.Table;
import de.scp.selector.ruleengine.rules.conditions.And;
import de.scp.selector.ruleengine.rules.conditions.Equals;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.conditions.IFuzzyBoolComponent;
import de.scp.selector.ruleengine.rules.consequences.AssignEquals;
import de.scp.selector.ruleengine.rules.consequences.Exclude;
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
		assertTrue("shortcut for 'lhs is false' failed",
				compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
		compound = new And(new Equals(numbers2, "4"), new Equals(numbers1, "1"));
		assertTrue("shortcut for 'rhs is false' failed",
				compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
		numbers2.select(sc, "4", 1);
		assertTrue("evaluation 'true and false' failed",
				compound.evaluate(sc) == FuzzyBoolEnum.FALSE);
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
		Enumeration numbers3 = new Enumeration("numbers3", new String[] { "1", "2", "3", "4" });
		Rule rule = new Rule(new And(new Equals(numbers1, "4"), new Equals(numbers2, "1")),
				new AssignEquals(numbers3, "3"));
		assertTrue("Execution should return true", rule.execute(sc, 1).hasFired());
		assertTrue("Value 3 hould be assigned", numbers3.equalsTo(sc, "3") == FuzzyBoolEnum.TRUE);
	}

	public void testClear() {
		Knowledgebase kb = new Knowledgebase();
		kb.createAttribute(new Enumeration("Car",
				new String[] { "Sportscar", "Van", "Compact car" }));
		kb.createAttribute(new Enumeration("Engine power", new String[] { "55 hp", "65 hp",
				"90 hp", "115 hp", "220 hp" }));
		kb.createAttribute(new Enumeration("Color", new String[] { "red", "black", "blue", "grey",
				"green" }));
		kb.createAttribute(new Enumeration("Navigation", new String[] { "yes", "no" }));
		kb.createAttribute(new Enumeration("Radio", new String[] { "no", "simple", "comfort" }));

		kb.createRule(new Table(new AbstractAttribute[] { kb.getAttribute("Car"),
				kb.getAttribute("Engine power"), kb.getAttribute("Color") }, new String[][] {
				{ "Sportscar", "220 hp", "red" }, { "Sportscar", "220 hp", "black" },
				{ "Van", "65 hp", "black" }, { "Van", "90 hp", "blue" },
				{ "Van", "115 hp", "grey" }, { "Compact car", "65 hp", "grey" },
				{ "Compact car", "65 hp", "grey" }, { "Compact car", "55 hp", "grey" },
				{ "Compact car", "55 hp", "green" } }));
		Session session = new Session(kb);
		session.setAttribute("Car", "Sportscar");
		session.setAttribute("Color", "blue");
		session.clear();
		session.setAttribute("Car", "Sportscar");
		assertEquals("Error after session clearing.", "220 hp", session
				.getSelectedValues("Engine power")[0]);
	}

	public void testPerformance() {
		int count = 1000;
		Knowledgebase kb = new Knowledgebase();
		for (int i = 0; i < count; i++) {
			kb.createAttribute(new Enumeration("enum" + i, new String[] { "a", "b", "c", "d" }));
		}
		for (int i = count - 1; i > 0; i--) {
			kb.createRule(new Rule(new Equals(kb.getAttribute("enum" + (i - 1)), "a"),
					new AssignEquals(kb.getAttribute("enum" + (i)), new String[] { "a" })));
		}
		Session session = new Session(kb);
		session.setAttribute("enum0", "a");
		assertEquals("Rule propagation failed.", "a", session.getSelectedValues("enum"
				+ (count - 1))[0]);
	}

}
