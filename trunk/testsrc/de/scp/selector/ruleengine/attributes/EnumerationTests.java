package de.scp.selector.ruleengine.attributes;

import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import junit.framework.TestCase;


public class EnumerationTests extends TestCase {

	public void testEquals() {
		Knowledgebase kb = new Knowledgebase();
		Enumeration enumeration = new Enumeration("numbers", new String[] { "1", "2", "3",
				"4", "5" });
		enumeration.select("4" , 1);
		assertTrue("Enumeration.equals(String) failed", enumeration
				.equalsTo("4") == FuzzyBoolEnum.TRUE);
		assertTrue("Enumeration.equals(String) failed", enumeration
				.equalsTo("3") == FuzzyBoolEnum.FALSE);
		assertTrue("Enummeration.assignEqual(String) failed", enumeration
				.assignEqual("4",1).hasChangedAttributes());

		enumeration.select(new String[] { "3", "5" });
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(new String[] { "5", "3" }) == FuzzyBoolEnum.TRUE);
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(new String[] { "3", "4" }) == FuzzyBoolEnum.FALSE);
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(new String[] { "3" }) == FuzzyBoolEnum.FALSE);
		assertTrue("Enummeration.assignEqual(String) failed", enumeration
				.assignEqual(new String[] { "5", "3" }, 1));

		enumeration = new Enumeration("numbers", new String[] { "1", "2", "3",
				"4", "5" });
		assertTrue("Enummeration.assignEqual(String) failed", enumeration
				.assignEqual(new String[] { "5", "3" }, 1));
	}
}
