package de.scp.selector.ruleengine.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;
import de.scp.selector.util.Logger;


/**
 * @author Axel Sammet
 */
public class Enumeration extends AbstractAttribute {

	public static class EnumElement {
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

		public String toString() {
			return name;
		}

		public String toFullString() {
			return name + (selected ? "*" : " ") + (available ? "   " : "n/a");
		}
	}

	private Enumeration.EnumElement emptyElement = new Enumeration.EnumElement("-");
	ArrayList<EnumElement> elements = new ArrayList<EnumElement>();

	public Enumeration(String name, String[] allElements) {
		super(name);
		elements.add(emptyElement);
		elements.get(0).selected = true;
		for (int i = 0; i < allElements.length; i++) {
			elements.add(new EnumElement(allElements[i]));
		}
	}

	/**
	 * Selects a single item in an enumeration. All other elements are
	 * deselected.
	 * 
	 * @param itemName
	 */
	// TODO check visibility
	public boolean select(String itemName, int sequence) {
		return select(new String[] { itemName }, sequence);
	}

	/**
	 * Selects the given items in an enumeration. All other elements are
	 * deselected.
	 * 
	 * @param items
	 */
	public boolean select(String[] items, int sequence) {
		for (EnumElement elem : getElements())
			elem.selected = false;
		if (items == null)
			return true;
		itemloop: for (int i = 0; i < items.length; i++) {
			for (EnumElement elem : getElements()) {
				if (items[i].equals(elem.name)) {
					if (getSequence() != 0 && getSequence() < sequence && !elem.available) {
						return false;
					}
					elem.selected = true;
					continue itemloop;
				}
			}

			throw new RuntimeException("element " + items[i] + " not in enumeration " + toString());
		}
		assigned = true;
		setSequence(sequence);
		return true;
	}

	/**
	 * @param applicableVals
	 */
	public IConsequence.Result include(String[] applicableVals, int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		int noOfIncludedElements = 0;
		EnumElement lastIncluded = null;
		for (EnumElement elem : getElements()) {
			if (elem.equals(emptyElement)) {
				continue;
			}
			boolean isIncluded = false;
			for (int i = 0; i < applicableVals.length; i++) {
				if (elem.name.equals(applicableVals[i])) {
					if (elem.sequence != 0 && getSequence() < sequence && !elem.available) {
						result.setViolation("Inclusion of excluded value " + elem.name
								+ " for attribute " + getName());
					}
					isIncluded = true;
					noOfIncludedElements++;
					lastIncluded = elem;
					if (elem.available == false) {
						elem.available = true;
						elem.sequence = sequence;
					}
					break;
				}
				else if (elem.sequence != 0 && getSequence() < sequence && elem.selected) {
					result.setViolation("Inclusion does not contain selected value " + elem.name
							+ " for attribute " + getName());

				}
			}
			if (!isIncluded) {
				if (elem.sequence != 0 && getSequence() < sequence && elem.selected) {
					result.setViolation("Inclusion does not contain selected value " + elem.name
							+ " for attribute " + getName());
				}
				elem.available = false;
				elem.sequence = sequence;
			}
		}
		if (noOfIncludedElements == 1) {
			select(lastIncluded.name, sequence);
		}
		setSequence(sequence);
		return result;
	}

	public IConsequence.Result exclude(String[] items, int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		for (int i = 0; i < items.length; i++) {
			IConsequence.Result singleRes = exclude(items[i], sequence);
			result.merge(singleRes);
		}
		return result;
	}

	public IConsequence.Result exclude(String item, int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		EnumElement anAvailableElement = null;
		int noOfAvailableElements = 0;
		for (EnumElement elem : getElements()) {
			if (item.equals(elem.name)) {
				if (getSequence() != 0 && getSequence() < sequence && elem.selected) {
					result.setViolation("Exclude " + item + " from " + getName()
							+ " conflicts with actual value.");
					return result;
				}
				if (elem.selected) {
					select("-", sequence);
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
			boolean selectionValid = select(anAvailableElement.name, sequence);
			if (!selectionValid) {
				result.setViolation("Exclude " + item + " from " + getName()
						+ " conflicts with actual value.");
			}
		}
		return result;
	}

	public FuzzyBoolEnum equalsTo(String item) {
		if (!assigned)
			return FuzzyBoolEnum.UNDEFINED;
		FuzzyBoolEnum foundSelection = FuzzyBoolEnum.FALSE;
		for (EnumElement elem : getElements()) {
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

	public FuzzyBoolEnum equalsTo(String[] itemArray) {
		if (!assigned)
			return FuzzyBoolEnum.UNDEFINED;
		List<String> items = new LinkedList<String>(Arrays.asList(itemArray));
		// we search all selected elements in items and remove found items
		elementsLoop: for (EnumElement elem : getElements()) {
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

	public FuzzyBoolEnum equalsTo(Object obj) {
		if (obj instanceof String)
			return equalsTo((String) obj);
		if (obj instanceof String[])
			return equalsTo((String[]) obj);
		return FuzzyBoolEnum.FALSE;
	}

	/**
	 * Equals the Enumeration with the given item. If the Enumeration is already
	 * assigned this method checks if the selected items match the given item.
	 * 
	 * @param item
	 * @return Returns true if the two operands are equal
	 */
	public IConsequence.Result assignEqual(String item, int sequence) {
		return assignEqual(new String[] { item }, sequence);
	}

	/**
	 * Equals the Enumeration with the given item. If the Enumeration is already
	 * assigned this method checks if the selected items match the given item.
	 * 
	 * @param item
	 * @return Returns true if the two operands are equal
	 */
	public IConsequence.Result assignEqual(String[] items, int sequence) {
		IConsequence.Result result = new IConsequence.Result();
		FuzzyBoolEnum equal = equalsTo(items);
		if (equal == FuzzyBoolEnum.TRUE) {
			return result;
		}
		else if (getSequence() != 0 && getSequence() < sequence && equal == FuzzyBoolEnum.FALSE) {
			result.setViolation(getName() + " = " + Logger.arrayToString(items)
					+ " is in conflict with actual value");
			return result;
		}
		boolean selectionValid = select(items, sequence);
		if (!selectionValid) {
			result.setViolation(getName() + " = " + Logger.arrayToString(items)
					+ " is in conflict with actual value");
			return result;
		}
		result.addAffectedAttribute(this);
		if (getSequence() > sequence)
			setSequence(sequence);
		for (EnumElement elem : getElements()) {
			boolean isSelected = false;
			for (int i = 0; i < items.length; i++) {
				if (elem.name.equals(items[i]))
					isSelected = true;
			}
			elem.available = isSelected;
		}
		return result;
	}

	public IConsequence.Result assignEqual(Object obj, int sequence) {
		if (obj instanceof String)
			return assignEqual((String) obj, sequence);
		if (obj instanceof String[])
			return assignEqual((String[]) obj, sequence);
		throw new RuntimeException("assignEqual(" + obj.getClass().getName() + ") not implemented");
	}

	/**
	 * @return selected values of this enumeration as EnumElement[].
	 */
	@Override
	public Object getValue() {
		ArrayList<EnumElement> ret = new ArrayList<EnumElement>();
		for (EnumElement elem : getElements())
			if (elem.selected)
				ret.add(elem);
		return (EnumElement[]) ret.toArray(new EnumElement[ret.size()]);
	}

	@Override
	public String toString() {
		String ret = getName() + "[";
		for (EnumElement elem : getElements())
			ret += elem.toFullString() + ", ";
		return ret + "]";
	}

	public ArrayList<EnumElement> getElements() {
		return elements;
	}

	public void removeExclusions() {
		for (EnumElement elem : getElements()) {
			elem.available = true;
		}
	}

	public void removeViolations() {
		super.removeViolations();
		removeExclusions();
	}

	@Override
	public void clear() {
		for (EnumElement elem : getElements()) {
			elem.available = true;
			elem.sequence = 0;
			if (elem.equals(emptyElement)) {
				elem.selected = true;
			}
			else {
				elem.selected = false;
			}
		}
		setSequence(0);
		removeViolations();
		assigned = false;
	}

}
