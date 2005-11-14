package de.scp.selector.ruleengine.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.scp.selector.ruleengine.SessionContents;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;
import de.scp.selector.util.Logger;
import de.scp.selector.util.PublicCloneable;

/**
 * Enumerations reflect attributes with a defined set of values. For an
 * explanation of the meaning of the often used parameter sequence see
 * AbstractAttribute.
 * 
 * @author Axel Sammet
 */
public class Enumeration extends AbstractAttribute {

	public static class EnumElement implements PublicCloneable {
		public String name;

		public boolean available = true;

		public boolean selected;

		private int sequence;

		public EnumElement(String name) {
			this.name = name;
		}

		public void exclude(int seq) {
			available = false;
			sequence = seq;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EnumElement))
				return false;
			EnumElement el = (EnumElement) obj;
			return (el.available == available && el.selected == selected && el.name
					.equals(name));
		}

		public PublicCloneable clone() {
			EnumElement ret = new EnumElement(this.name);
			ret.available = this.available;
			ret.selected = this.selected;
			ret.sequence = this.sequence;
			return ret;
		}

		public String toString() {
			return name;
		}

		public String toFullString() {
			return name + (selected ? "*" : " ") + (available ? "   " : "n/a");
		}
	}

	private static class EnumerationContents implements PublicCloneable {
		private List<EnumElement> elements = new ArrayList<EnumElement>();

		public List<EnumElement> getElements() {
			return elements;
		}

		public void init(String[] elementNames) {
			elements.add(emptyElement);
			elements.get(0).selected = true;
			for (int i = 0; i < elementNames.length; i++) {
				elements.add(new EnumElement(elementNames[i]));
			}
		}

		@Override
		public PublicCloneable clone() {
			EnumerationContents ret = new EnumerationContents();
			Iterator<EnumElement> iter = elements.iterator();
			while ( iter.hasNext()) {
				EnumElement element = iter.next();
				ret.elements.add(element);
			}
			return ret;
		}
	}

	private static Enumeration.EnumElement emptyElement = new Enumeration.EnumElement(
			"-");

	String[] elementNames;

	public Enumeration(String name, String[] allElements) {
		super(name);
		elementNames = allElements;
	}

	/**
	 * Selects a single item in an enumeration. All other elements are
	 * deselected.
	 * 
	 * @param itemName
	 */
	// TODO check visibility
	public boolean select(SessionContents sc, String itemName, int sequence) {
		return select(sc, new String[] { itemName }, sequence);
	}

	/**
	 * Selects the given items in an enumeration. All other elements are
	 * deselected.
	 * 
	 * @param items
	 */
	// TODO buggy when selecting an assigned attribute
	public boolean select(SessionContents sc, String[] items, int sequence) {
		if (items == null)
			return false;
		// deslect attribute
		if (items.length==1 && emptyElement.name.equals(items[0])) {
			clear(sc);
			return true;
		}
		// identify selected elements and set them
		itemloop: for (int i = 0; i < items.length; i++) {
			for (EnumElement elem : getElements(sc)) {
				boolean wasSelected = elem.selected;
				elem.selected = false;
				if (items[i].equals(elem.name)) {
					// TODO a violation would be better
					if (getSequence() != 0
							&& getSequence() < sequence
							&& (!elem.available || (!wasSelected && assigned))) {
						return false;
					}
					elem.selected = true;
					assigned = true;
					setSequence(sequence);
					continue itemloop;
				}
			}
			throw new RuntimeException("element " + items[i]
					+ " not in enumeration " + toString());
		}
		return true;
	}


	/**
	 * Excludes all parameter, which are not given in the parameter
	 * applicableVals.
	 * 
	 * @param applicableVals
	 * @param sequence
	 */
	public IConsequence.Result include(SessionContents sc,
			String[] applicableVals, int sequence) {
		// TODO A better implementation might be to identify all not
		// applicable values and then call exclude on them (and map violation
		// texts)
		IConsequence.Result result = new IConsequence.Result();
		int noOfIncludedElements = 0;
		EnumElement lastIncluded = null;
		for (EnumElement elem : getElements(sc)) {
			if (elem.equals(emptyElement)) {
				continue;
			}
			boolean isIncluded = false;
			for (int i = 0; i < applicableVals.length; i++) {
				if (elem.name.equals(applicableVals[i])) {
					if (elem.sequence != 0 && getSequence() < sequence
							&& !elem.available) {
						result.setViolation("Inclusion of excluded value "
								+ elem.name + " for attribute " + getName());
					}
					isIncluded = true;
					noOfIncludedElements++;
					lastIncluded = elem;
					if (elem.available == false) {
						elem.available = true;
						elem.sequence = sequence;
					}
					break;
				} else if (elem.sequence != 0 && getSequence() < sequence
						&& elem.selected) {
					result
							.setViolation("Inclusion does not contain selected value "
									+ elem.name + " for attribute " + getName());

				}
			}
			if (!isIncluded) {
				if (elem.sequence != 0 && getSequence() < sequence
						&& elem.selected) {
					result
							.setViolation("Inclusion does not contain selected value "
									+ elem.name + " for attribute " + getName());
				}
				// deselect the value if it is already selected
				elem.available = false;
				if (elem.selected) {
					select(sc, "-", 0);
				}
				elem.sequence = sequence;
			}
		}
		if (noOfIncludedElements == 1) {
			select(sc, lastIncluded.name, sequence);
		}
		setSequence(sequence);
		return result;
	}

	/**
	 * Excludes an array of values. 
	 * @param sc
	 * @param items
	 * @param sequence
	 * @return
	 */
	public IConsequence.Result exclude(SessionContents sc, String[] items,
			int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		for (int i = 0; i < items.length; i++) {
			IConsequence.Result singleRes = exclude(sc, items[i], sequence);
			result.merge(singleRes);
		}
		return result;
	}

	/**
	 * Excludes an item from this enumeration (EnumElement.applicable = false).
	 * If only one applicable element is left it will be selected.
	 * 
	 * @param item
	 *            Item to exclude.
	 * @param sequence
	 *            sequence
	 * @return
	 */
	public IConsequence.Result exclude(SessionContents sc, String item,
			int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		EnumElement anAvailableElement = null;
		int noOfAvailableElements = 0;
		for (EnumElement elem : getElements(sc)) {
			// do not exclude the empty element
			if (elem.equals(emptyElement))
				continue;
			if (item.equals(elem.name)) {
				if (getSequence() != 0 && getSequence() < sequence
						&& elem.selected) {
					result.setViolation("Exclude '" + item
							+ "' from attribute " + getName()
							+ " conflicts with actual value.");
					return result;
				}
				if (elem.selected) {
					select(sc, emptyElement.name, sequence);
				}
				elem.exclude(sequence);
			}
			// count elements which are still available
			if (!elem.equals(emptyElement) && elem.available) {
				anAvailableElement = elem;
				noOfAvailableElements++;
			}
		}
		// if only one available elment is left, select it
		if (noOfAvailableElements == 1) {
			boolean selectionValid = select(sc, anAvailableElement.name,
					sequence);
			if (!selectionValid) {
				result.setViolation("Exclude " + item + " from " + getName()
						+ " conflicts with actual value.");
			}
		}
		return result;
	}

	public FuzzyBoolEnum equalsTo(SessionContents sc, String item) {
		if (!assigned)
			return FuzzyBoolEnum.UNDEFINED;
		FuzzyBoolEnum foundSelection = FuzzyBoolEnum.FALSE;
		for (EnumElement elem : getElements(sc)) {
			if (elem.selected) {
				// this is the selection expected
				if (elem.name.equals(item))
					foundSelection = FuzzyBoolEnum.TRUE;
				// different selections
				else
					return FuzzyBoolEnum.FALSE;
			}
		}
		return foundSelection;
	}

	public FuzzyBoolEnum equalsTo(SessionContents sc, String[] itemArray) {
		if (!assigned)
			return FuzzyBoolEnum.UNDEFINED;
		List<String> items = new LinkedList<String>(Arrays.asList(itemArray));
		// we search all selected elements in items and remove found items
		elementsLoop: for (EnumElement elem : getElements(sc)) {
			if (elem.selected) {
				for (Iterator<String> iter = items.iterator(); iter.hasNext();) {
					String item = iter.next();
					if (item.equals(elem.name)) {
						iter.remove();
						continue elementsLoop;
					}
				}
				// selected element is not in items
				return FuzzyBoolEnum.FALSE;
			}
		}
		if (items.size() == 0)
			return FuzzyBoolEnum.TRUE;
		// some items are left and obviously not selected
		else
			return FuzzyBoolEnum.FALSE;
	}

	public FuzzyBoolEnum equalsTo(SessionContents sc, Object obj) {
		if (obj instanceof String)
			return equalsTo(sc, (String) obj);
		if (obj instanceof String[])
			return equalsTo(sc, (String[]) obj);
		return FuzzyBoolEnum.FALSE;
	}

	/**
	 * Equals the Enumeration with the given item. If the Enumeration is already
	 * assigned this method checks if the selected items match the given item.
	 * 
	 * @param item
	 * @return Returns true if the two operands are equal
	 */
	public IConsequence.Result assignEqual(SessionContents sc, String item,
			int sequence) {
		return assignEqual(sc, new String[] { item }, sequence);
	}

	/**
	 * Equals the Enumeration with the given item. If the Enumeration is already
	 * assigned this method checks if the selected items match the given item.
	 * 
	 * @param item
	 * @return Returns true if the two operands are equal
	 */
	public IConsequence.Result assignEqual(SessionContents sc, String[] items,
			int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		FuzzyBoolEnum equal = equalsTo(sc, items);
		if (equal == FuzzyBoolEnum.TRUE) {
			return result;
		} else if (getSequence() != 0 && getSequence() < sequence
				&& equal == FuzzyBoolEnum.FALSE) {
			result.setViolation(getName() + " = " + Logger.arrayToString(items)
					+ " is in conflict with actual value");
			return result;
		}
		boolean selectionValid = select(sc, items, sequence);
		if (!selectionValid) {
			result.setViolation(getName() + " = " + Logger.arrayToString(items)
					+ " is in conflict with actual value");
			return result;
		}
		result.addAffectedAttribute(this);
		if (getSequence() > sequence)
			setSequence(sequence);
		for (EnumElement elem : getElements(sc)) {
			boolean isSelected = false;
			for (int i = 0; i < items.length; i++) {
				if (elem.name.equals(items[i]))
					isSelected = true;
			}
			elem.available = isSelected;
		}
		return result;
	}

	public IConsequence.Result assignEqual(SessionContents sc, Object obj,
			int sequence) {
		if (obj instanceof String)
			return assignEqual(sc, (String) obj, sequence);
		if (obj instanceof String[])
			return assignEqual(sc, (String[]) obj, sequence);
		throw new RuntimeException("assignEqual(" + obj.getClass().getName()
				+ ") not implemented");
	}

	/**
	 * @return selected values of this enumeration as EnumElement[].
	 */
	@Override
	public Object getValue(SessionContents sc) {
		ArrayList<EnumElement> ret = new ArrayList<EnumElement>();
		for (EnumElement elem : getElements(sc))
			if (elem.selected)
				ret.add(elem);
		return (EnumElement[]) ret.toArray(new EnumElement[ret.size()]);
	}

	public String toString(SessionContents sc) {
		String ret = getName() + "[";
		for (EnumElement elem : getElements(sc))
			ret += elem.toFullString() + ", ";
		return ret + "]";
	}

	public List<EnumElement> getElements(SessionContents sc) {
		EnumerationContents contents = (EnumerationContents) getContents(sc);
		if (contents == null) {
			contents = new EnumerationContents();
			contents.init(elementNames);
			if (sc != null) {
				sc.putContents(getName(), contents);
			}
		}
		return contents.getElements();
	}

	public String[] getElementNames() {
		return elementNames;
	}

	public void removeExclusions(SessionContents sc) {
		for (EnumElement elem : getElements(sc)) {
			elem.available = true;
		}
	}

	public void removeViolations(SessionContents sc) {
		super.removeViolations();
		removeExclusions(sc);
	}

	@Override
	public void clear(SessionContents sc) {
		for (EnumElement elem : getElements(sc)) {
			elem.available = true;
			elem.sequence = 0;
			if (elem.equals(emptyElement)) {
				elem.selected = true;
			} else {
				elem.selected = false;
			}
		}
		setSequence(0);
		removeViolations();
		assigned = false;
	}

	@Override
	public Object[] getAllValues(SessionContents sc) {
		return (Object[]) getElements(sc).toArray(
				new Object[getElements(sc).size()]);
	}

}
