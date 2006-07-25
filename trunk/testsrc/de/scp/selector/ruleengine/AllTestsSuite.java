package de.scp.selector.ruleengine;

import junit.framework.TestSuite;
import de.scp.selector.ruleengine.attributes.EnumerationTests;
import de.scp.selector.ruleengine.rules.RuleTests;

public class AllTestsSuite extends TestSuite {
	public AllTestsSuite() {

		addTestSuite(EnumerationTests.class);
		addTestSuite(RuleTests.class);
	}

	public void testSuite() {
	}
}
