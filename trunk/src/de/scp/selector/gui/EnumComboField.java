package de.scp.selector.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import de.scp.selector.ruleengine.Session;
import de.scp.selector.ruleengine.attributes.Enumeration;

public class EnumComboField extends JComboBox {
	private String linkedAttrName;
	private Session session;
	private JLabel label;
	private JLabel errorImageLabel;
	private static ImageIcon errorBubbleIcon;
	
	static {
		URL url = ClassLoader.getSystemResource("de/scp/selector/gui/resource/ErrorBubble_hor.png");
		errorBubbleIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		
	}

	public EnumComboField(String nameOfEnum, Session session) {
		this.linkedAttrName = nameOfEnum;
		this.session = session;
		// setForeground(Color.RED);
		label = new JLabel(linkedAttrName);
		errorImageLabel = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(16, 16);
			}
		};
		for (Object element : session.getValueContents(linkedAttrName)) {
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
		session.setAttribute(linkedAttrName, items[0]);
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
		// now do the update
		Object[] values = (Object[]) session.getValueContents(linkedAttrName);
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
		if (session.getViolations(linkedAttrName).length != 0) {
			errorImageLabel.setIcon(errorBubbleIcon);
			errorImageLabel.setToolTipText(session.getViolations(linkedAttrName)[0]);
//			label.setForeground(Color.RED);
//			setForeground(Color.RED);
		}
		else {
			setToolTipText(null);
			label.setForeground(Color.BLACK);
			setForeground(Color.BLACK);
			errorImageLabel.setIcon(null);
		}
		// Enumeration.EnumElement item = (Enumeration.EnumElement) getSelectedItem();
		// Logger.getInstance().debug("Select " + item.name + " " +
		// item.available);

		// reset listeners
		for (int i = 0; i < listeners.length; i++) {
			addActionListener(listeners[listeners.length - i - 1]);
		}
	}

	public Component getMarker() {
		return errorImageLabel;
	}

}
