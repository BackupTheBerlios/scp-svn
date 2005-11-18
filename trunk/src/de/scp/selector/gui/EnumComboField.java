package de.scp.selector.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
		label = new JLabel(linkedAttr.getName());
		for (Object element : session.getValueContents(linkedAttr.getName())) {
			addItem(element);
		}
		setRenderer(new BasicComboBoxRenderer() {
			public Component getListCellRendererComponent(javax.swing.JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				Component ret = super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				if (!((Enumeration.EnumElement) value).available) {
					ret.setForeground(Color.RED);
				}
				return ret;
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
			items[i] = ((Enumeration.EnumElement) selection[i]).name;
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
		Object[] values = (Object[]) session
				.getValueContents(linkedAttr.getName());
		if (values != null && values.length != 0) {
			removeAllItems();
			for (int i = 0; i < values.length; i++) {
				addItem(values[i]);
				if (((Enumeration.EnumElement)values[i]).selected) {
					setSelectedIndex(i);
				}
			}
		}
		if (linkedAttr.getViolations().length != 0) {
			label.setToolTipText(linkedAttr.getViolations()[0]);
			label.setForeground(Color.RED);
		}
		else {
			label.setToolTipText(null);
			label.setForeground(Color.BLACK);
		}

		for (int i = 0; i < listeners.length; i++) {
			addActionListener(listeners[listeners.length - i - 1]);
		}
	}

}
