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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;


public class DefaultGuiFrame extends JFrame {
	private Session session;
	private List<EnumComboField> inputElements = new ArrayList<EnumComboField>();
	JCheckBox forceInputCheckBox;
	
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
		JButton sendButton = new JButton("clear");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session.clear();
				updateAll();
			}
		});
		innerPanel.add(sendButton);
		forceInputCheckBox = new JCheckBox("force");
		forceInputCheckBox.setToolTipText("Allow selection of not applicable values.");
		innerPanel.add(forceInputCheckBox);
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

}
