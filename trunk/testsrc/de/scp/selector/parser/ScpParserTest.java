package de.scp.selector.parser;

import junit.framework.TestCase;
import de.scp.selector.parser.generated.ScpParser;
import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.Session;

public class ScpParserTest extends TestCase {

	public void testBooleanLogic() {
		String[][] testCases = { { "true and false", "false" }, { "true and true", "true" },
				{ "false and false", "false" }, { "false and true", "false" },

				{ "true or false", "true" }, { "true or true", "true" },
				{ "false or false", "false" }, { "false or true", "true" },

				{ "false and true or false", "false" }, { "false or true and false", "false" },
				{ "true or true and false", "true" }, { "false and true or true", "true" },

				{ "(true or true) and false", "false" },

				{ "not true", "false" }, { "not false", "true" },
				{ "not (true and false)", "true" },

				{ "(true and not false) or (not true and false)", "true" }

		};
		// for (int i = 0; i < testCases.length; i++) {
		// assertTrue(testCases[i][0] + " expected " + testCases[i][1] + ", but failed", Boolean
		// .parseBoolean(testCases[i][1]) == ScpParser.evaluate(testCases[i][0]));
		// }
	}

	public void testRules() {
		String[][] testCases = { new String[] {
				"knowledgebase Armin_01; \n" 
						+ "  define enum a = {\"aa\",\"bb\"}; \n"
						+ "  define enum b = {\"aa\",\"bb\"}; \n"
						+ "  if (a=\"aa\") then b = \"bb\";\n" 
						+ "end\n", 
						"a", "aa", "b", "bb" } 
		};
		for (int i = 0; i < testCases.length; i++) {
			System.out.println(testCases[i][0]);
			Knowledgebase kb = ScpParser.compileKnowledgebase(testCases[i][0]);
			Session session = new Session(kb);
			session.setAttribute(testCases[i][1], testCases[i][2]);
			assertEquals(testCases[i][0] + testCases[i][1] + " = " + testCases[i][2] +
					" => "+testCases[i][3],
					testCases[i][4], session.getSelectedValues(testCases[i][3])[0]);
		}
	}
}
