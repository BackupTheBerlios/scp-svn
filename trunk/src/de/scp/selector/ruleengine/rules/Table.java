package de.scp.selector.ruleengine.rules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;
import de.scp.selector.ruleengine.attributes.Enumeration;
import de.scp.selector.ruleengine.rules.conditions.FuzzyBoolEnum;
import de.scp.selector.ruleengine.rules.consequences.IConsequence;


public class Table extends AbstractRule {
	AbstractAttribute[] columns;
	String[][] values;

	/**
	 * @param columns
	 * @param values
	 */
	public Table(AbstractAttribute[] columns, String[][] values) {
		this.columns = columns;
		this.values = values;
	}

	@Override
	protected Result internalExecute(int sequence) {
		IConsequence.Result conseq = new IConsequence.Result();
		// identify all assigned columns
		Set<String>[] valueRanges = new Set[columns.length];
		List<Integer> assignedIndices = new LinkedList<Integer>();
		for (int i = 0; i < columns.length; i++) {
			AbstractAttribute attr = columns[i];
			if (attr.isAssigned()) {
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
			String[] applicableVals = (String[]) valueRanges[j].toArray(new String[valueRanges[j]
					.size()]);
			if (columns[j] instanceof Enumeration && !columns[j].isAssigned()) {
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

}
