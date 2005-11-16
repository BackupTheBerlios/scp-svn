import de.scp.selector.gui.DefaultGuiFrame;
import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.Rule;
import de.scp.selector.ruleengine.rules.Table;
import de.scp.selector.ruleengine.rules.conditions.Equals;
import de.scp.selector.ruleengine.rules.consequences.Exclude;

public class CarSelling {
	
	public static void main(String[] args) {
		Knowledgebase kb = new Knowledgebase();
		kb.createAttribute(new Enumeration("Car", new String[] { "Sportscar",
				"Van", "Compact car"}));
		kb.createAttribute(new Enumeration("Engine power", new String[] {
				"55 hp", "65 hp", "90 hp", "115 hp", "220 hp" }));
		kb.createAttribute(new Enumeration("Color", new String[] {
				"red", "black", "blue", "grey", "green"}));
		kb.createAttribute(new Enumeration("Navigation", new String[] {
				"yes", "no"}));
		kb.createAttribute(new Enumeration("Radio", new String[] {
				"no", "simple", "comfort"}));

		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("Car"), kb.getAttribute("Engine power"), kb.getAttribute("Color")},
				new String[][] {
						{"Sportscar", "220 hp", "red"}, 
						{"Sportscar", "220 hp", "black"}, 
						{"Van", "65 hp", "black"}, 
						{"Van", "90 hp", "blue"}, 
						{"Van", "115 hp", "grey"}, 
						{"Compact car", "65 hp", "grey"},
						{"Compact car", "65 hp", "grey"},
						{"Compact car", "55 hp", "grey"},
						{"Compact car", "55 hp", "green"}}
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Navigation"), "yes"),
				new Exclude(kb.getAttribute("Radio"), new String[] {"no"})
				));
		Session session = new Session(kb);
		DefaultGuiFrame frame = new DefaultGuiFrame(session);
		frame.setVisible(true);
	}


}
