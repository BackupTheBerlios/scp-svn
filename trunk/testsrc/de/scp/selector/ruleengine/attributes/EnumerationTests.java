package de.scp.selector.ruleengine.attributes;

import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import junit.framework.TestCase;


public class EnumerationTests extends TestCase {
	class TestSessionContents extends SessionContents {
		public TestSessionContents() {
		}
	}

	public void testEquals() {
		Knowledgebase kb = new Knowledgebase();
		SessionContents sc = new TestSessionContents();
		Enumeration enumeration = new Enumeration("numbers", new String[] { "1", "2", "3",
				"4", "5" });
		enumeration.select(sc, "4" , 1);
		assertTrue("Enumeration.equals(String) failed", enumeration
				.equalsTo(sc, "4") == FuzzyBoolEnum.TRUE);
		assertTrue("Enumeration.equals(String) failed", enumeration
				.equalsTo(sc, "3") == FuzzyBoolEnum.FALSE);
		assertTrue("Enummeration.assignEqual(String) failed", !enumeration
				.assignEqual(sc, "4",1).isViolated());

		enumeration.select(sc, new String[] { "3", "5" }, 1);
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(sc, new String[] { "5", "3" }) == FuzzyBoolEnum.TRUE);
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(sc, new String[] { "3", "4" }) == FuzzyBoolEnum.FALSE);
		assertTrue("Enumeration.equals(String[]) failed", enumeration
				.equalsTo(sc, new String[] { "3" }) == FuzzyBoolEnum.FALSE);
		assertTrue("Enummeration.assignEqual(String) failed", !enumeration
				.assignEqual(sc, new String[] { "5", "3" }, 1).isViolated());

		enumeration = new Enumeration("numbers", new String[] { "1", "2", "3",
				"4", "5" });
		assertTrue("Enummeration.assignEqual(String) failed", !enumeration
				.assignEqual(sc, new String[] { "5", "3" }, 1).isViolated());
	}
}
