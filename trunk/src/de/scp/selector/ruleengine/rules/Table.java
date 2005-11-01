package de.scp.selector.ruleengine.rules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;
import de.scp.selector.util.Logger;

/**
 * In a Table you can define valid value combinations for a given set of
 * attributes. Each attribute matches a column, each row represents one valid
 * set of values.
 * 
 * @author Axel Sammet
 */
// TODO implement a special value to ignore the column in this case
public class Table extends AbstractRule {
	AbstractAttribute[] columns;

	String[][] values;

	/**
	 * @param columns
	 * @param values
	 *            Read index like [row][column].
	 */
	public Table(AbstractAttribute[] columns, String[][] values) {
		this.columns = columns;
		this.values = values;
	}

	@Override
	protected Result internalExecute(int sequence) {
		IConsequence.Result conseq = new IConsequence.Result();
		// identify all columns (attributes) assigned
		// ignoring columns which have been set by this table
		Set<String>[] valueRanges = new Set[columns.length];
		List<Integer> assignedIndices = new LinkedList<Integer>();
		for (int i = 0; i < columns.length; i++) {
			AbstractAttribute attr = columns[i];
			if (attr.isAssigned() && attr.getSequence() < sequence) {
				assignedIndices.add(new Integer(i));
			}
			valueRanges[i] = new HashSet<String>();
		}
		// iterate over rows and find included values per column
		for (int i = 0; i < values.length; i++) {
			boolean matchingRow = true;
			for (Integer col : assignedIndices) {
				if (columns[col.intValue()].equalsTo(values[i][col.intValue()]).equals(
						FuzzyBoolEnum.FALSE)) {
					matchingRow = false;
					break;
				}
			}
			if (matchingRow) {
				for (int j = 0; j < values[0].length; j++) {
					valueRanges[j].add(values[i][j]);
				}
			}
		}
		// for Enumerations include values
		for (int j = 0; j < values[0].length; j++) {
			// ignore the currently set attribute
			if (columns[j].getSequence() == 1)
				continue;
			String[] applicableVals = (String[]) valueRanges[j].toArray(new String[valueRanges[j]
					.size()]);
			if (columns[j] instanceof Enumeration) {
				Logger.getInstance().debug(
						"Table: " + columns[j].getName() + " includes: "
								+ Logger.arrayToString(applicableVals));
				if (applicableVals.length == 0) {
					conseq.setViolation("Invalid value combination "
							+ getColumnNames());
				}
				IConsequence.Result res = ((Enumeration) columns[j]).include(applicableVals,
						sequence);
				conseq.merge(res);
			}
		}
		// for identified arbits set value
		AbstractRule.Result result = new AbstractRule.Result(conseq);
		result.setHasFired(true);
		return result;
	}

	@Override
	public List<AbstractAttribute> dependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer("Table: ").append(getColumnNames()).append(": {");
		for (int row = 0; row < values.length; row++) {
			if (row != 0) {
				ret.append(", ");
			}
			ret.append("{");
			for (int col = 0; col < values[0].length; col++) {
				if (col != 0) {
					ret.append(", ");
				}
				ret.append(values[row][col]);
			}
			ret.append("}");
		}
		ret.append("}");
		return ret.toString();
	}

	/**
	 * @return
	 */
	private String getColumnNames() {
		StringBuffer ret = new StringBuffer("{");
		for (int col = 0; col < columns.length; col++) {
			if (col != 0) {
				ret.append(", ");
			}
			ret.append(columns[col].getName());
		}
		ret.append("}");
		return ret.toString();
	}

}
