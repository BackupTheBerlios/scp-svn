package de.scp.selector.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.Enumeration;

public class EnumComboField extends JComboBox {
	private Enumeration linkedAttr;
	private Session session;
	private JLabel label;

	public EnumComboField(Enumeration enumeration, Session session) {
		this.linkedAttr = enumeration;
		this.session = session;
		// setForeground(Color.RED);
		label = new JLabel(linkedAttr.getName());
		for (Object element : session.getValueContents(linkedAttr.getName())) {
			addItem(element);
		}
		setRenderer(new BasicComboBoxRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Component ret = super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				boolean itemIsAvailable = ((Enumeration.EnumElement) value).available;
				ret.setForeground(itemIsAvailable ? Color.BLACK : Color.RED);
				// ret.setBackground(isSelected ? Color.BLUE : Color.WHITE);
				// ret.setOpaque(true);
				return ret;
			}
		});
		addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		});
	}

	public JComponent getLabel() {
		return label;
	}

	public void transferSelectionToModel() {
		Object[] selection = getSelectedObjects();
		String[] items = new String[selection.length];
		for (int i = 0; i < items.length; i++) {
			Enumeration.EnumElement elem = (Enumeration.EnumElement) selection[i];
			if (!elem.available) {
				setSelectedIndex(0);
				return;
			}
			items[i] = elem.name;
		}
		session.setAttribute(linkedAttr.getName(), items[0]);
	}

	/**
	 * Updates the gui element with the values from the ruleengine.
	 */
	public void update() {
		// the important thing is not to send Events while updating, as this
		// results in endless looping. Here we are creating Action- and
		// ItemEvents, thus both should be removed and restored.
		ActionListener[] listeners = getActionListeners();
		for (int i = 0; i < listeners.length; i++) {
			removeActionListener(listeners[i]);
		}
		Object[] values = (Object[]) session.getValueContents(linkedAttr.getName());
		if (values != null && values.length != 0) {
			removeAllItems();
			for (int i = 0; i < values.length; i++) {
				addItem(values[i]);
				Enumeration.EnumElement item = (Enumeration.EnumElement) values[i];
				if (item.selected && item.available) {
					setSelectedIndex(i);
				}
			}
		}
		if (linkedAttr.getViolations().length != 0) {
			setToolTipText(linkedAttr.getViolations()[0]);
			label.setForeground(Color.RED);
			setForeground(Color.RED);
		}
		else {
			setToolTipText(null);
			label.setForeground(Color.BLACK);
			setForeground(Color.BLACK);
		}
		Enumeration.EnumElement item = (Enumeration.EnumElement) getSelectedItem();
		// Logger.getInstance().debug("Select " + item.name + " " +
		// item.available);

		// reset listeners
		for (int i = 0; i < listeners.length; i++) {
			addActionListener(listeners[listeners.length - i - 1]);
		}
	}

}
