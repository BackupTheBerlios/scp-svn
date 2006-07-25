import de.scp.selector.gui.DefaultGuiFrame;
import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.Table;

public class ChangeBandwidth {

	public static void main(String[] args) {
		Knowledgebase kb = new Knowledgebase();
		kb.createAttribute(new Enumeration("Damping", new String[] { "5 dB",
				"20 dB", "30 dB", "37 dB"}));
		kb.createAttribute(new Enumeration("ASB", new String[] {
				"green 2+", "green", "grey" }));
		kb.createAttribute(new Enumeration("Downstream", new String[] {
				"1000", "2000", "3000", "6000", "16000"}));
		kb.createAttribute(new Enumeration("Upstream", new String[] {
				"Standard", "384", "512"}));

		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("Damping"), kb.getAttribute("Upstream")},
				new String[][] {
						{"5 dB", "Standard"},
						{"5 dB", "384"},
						{"5 dB", "512"},
						{"20 dB", "Standard"},
						{"20 dB", "384"},
						{"20 dB", "512"},
						{"30 dB", "Standard"},
						{"30 dB", "384"},
						{"37 dB", "Standard"}
				}));
		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("Damping"), kb.getAttribute("Downstream")},
				new String[][] {
						{"5 dB", "1000"},
						{"5 dB", "2000"},
						{"5 dB", "3000"},
						{"5 dB", "6000"},
						{"5 dB", "16000"},
						{"20 dB", "1000"},
						{"20 dB", "2000"},
						{"20 dB", "3000"},
						{"20 dB", "16000"},
						{"30 dB", "1000"},
						{"30 dB", "2000"},
						{"30 dB", "3000"},
						{"37 dB", "1000"}
				}));
		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("Downstream"), kb.getAttribute("Upstream")},
				new String[][] {
						{"1000", "Standard"},
						{"2000", "Standard"},
						{"2000", "384"},
						{"3000", "Standard"},
						{"3000", "512"},
						{"6000", "Standard"},
						{"16000", "Standard"}
				}));
		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("ASB"), kb.getAttribute("Downstream")},
				new String[][] {
						{"green 2+", "1000"},
						{"green 2+", "2000"},
						{"green 2+", "3000"},
						{"green 2+", "6000"},
						{"green 2+", "16000"},
						{"green", "1000"},
						{"green", "2000"},
						{"green", "3000"},
						{"green", "6000"}
				}));
		Session session = new Session(kb);
		DefaultGuiFrame frame = new DefaultGuiFrame(session);
		frame.setVisible(true);
	}

}
