package de.scp.selector.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import de.scp.selector.ruleengine.Knowledgebase;
import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.Rule;
import de.scp.selector.ruleengine.rules.Table;
import de.scp.selector.ruleengine.rules.conditions.Equals;
import de.scp.selector.ruleengine.rules.conditions.Not;
import de.scp.selector.ruleengine.rules.consequences.Exclude;


public class DefaultGuiFrame extends JFrame {
	private Session session;
	private List<EnumComboField> inputElements = new ArrayList<EnumComboField>();
	
	public DefaultGuiFrame(Session session) {
		super("DefaultGui");
		this.session = session;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(1);
			}
		});
		getContentPane().add(getAttributePanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		pack();
		allignGUI();
	}

	/**
	 * 
	 */
	private void allignGUI() {
		int x = getToolkit().getScreenSize().width / 2
				- getPreferredSize().width / 2;
		int y = getToolkit().getScreenSize().height / 2
				- getPreferredSize().height / 2;
		setLocation(x, y);
	}

	/**
	 * @return
	 */
	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JPanel innerPanel = new JPanel(new GridLayout());
		JButton sendButton = new JButton("löschen");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session.clear();
				updateAll();
			}
		});
		innerPanel.add(sendButton);
		buttonPanel.add(innerPanel);
		return buttonPanel;
	}

	/**
	 * @param attribPanel
	 */
	private JPanel getAttributePanel() {
		JPanel attribPanel = new JPanel(new GridBagLayout());
		attribPanel.setBorder(new EtchedBorder());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		for (AbstractAttribute attrib : session.getAllAttributes()) {
			EnumComboField comp = (EnumComboField) createEnumField((Enumeration) attrib);
			gbc.gridx = 0;
			attribPanel.add(comp.getLabel(), gbc);
			gbc.gridx = 1;
			attribPanel.add(comp, gbc);
			gbc.gridy++;
		}
		return attribPanel;
	}

	/**
	 * @param enumeration
	 * @return
	 */
	private Component createEnumField(final Enumeration enumeration) {
		EnumComboField field = new EnumComboField(enumeration, this.session);
		inputElements.add(field);
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((EnumComboField) e.getSource()).transferSelectionToModel();
				updateAll();
			}});
		return field;
	}
	
	/**
	 * Visual update for all attributes.
	 */
	public void updateAll() {
		for (EnumComboField inputElement : inputElements ) {
			inputElement.update();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Knowledgebase kb = new Knowledgebase();
		kb.createAttribute(new Enumeration("Bandbreite", new String[] { "1000",
				"2000", "3000", "6000" }));
		kb.createAttribute(new Enumeration("Upstream", new String[] {
				"Standard", "384", "512", "1024" }));
		kb.createAttribute(new Enumeration("Online Tarif", new String[] {
				"Flatrate", "Volume 2000", "Volume 6000", "Minutentarif", "Superfast"}));
		kb.createAttribute(new Enumeration("AOC-D", new String[] {
				"an", "aus"}));
		kb.createAttribute(new Enumeration("AOC-E", new String[] {
				"an", "aus"}));

		kb.createRule(new Table(
				new AbstractAttribute[] {kb.getAttribute("AOC-D"), kb.getAttribute("AOC-E")},
				new String[][] {{"an", "aus"}, {"aus", "an"}}
				));
//		kb.createRule(new Rule(
//				new Equals(kb.getAttribute("Bandbreite"), "6000"),
//				new AssignEquals(kb.getAttribute("Upstream"), "1024")
//				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Bandbreite"), "2000"),
				new Exclude(kb.getAttribute("Upstream"), new String[] {"512", "1024"})
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Bandbreite"), "3000"),
				new Exclude(kb.getAttribute("Upstream"), new String[] {"384", "1024"})
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Bandbreite"), "6000"),
				new Exclude(kb.getAttribute("Upstream"), new String[] {"512", "384"})
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Bandbreite"), "1000"),
				new Exclude(kb.getAttribute("Upstream"), new String[] {"384", "512", "1024"})
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Bandbreite"), "3000"),
				new Exclude(kb.getAttribute("Online Tarif"), new String[] {"Minutentarif", "Flatrate"})
				));
		kb.createRule(new Rule(
				new Equals(kb.getAttribute("Upstream"), "512"),
				new Exclude(kb.getAttribute("Online Tarif"), new String[] {"Volume 2000"})
				));
		kb.createRule(new Rule(
				new Not(new Equals(kb.getAttribute("Upstream"), "1024")),
				new Exclude(kb.getAttribute("Online Tarif"), new String[] {"Superfast"})
				));
		Session session = new Session(kb);
		DefaultGuiFrame frame = new DefaultGuiFrame(session);
		frame.setVisible(true);
	}

}
