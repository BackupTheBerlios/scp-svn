package de.scp.selector.ruleengine;

import java.util.Collection;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;

/**
 * Session is one of the interface classes of SCP. Session provides methods to
 * set and get attributes and values. Session stores the SessionContents, which
 * is not part of the public interface.
 * 
 * @author Axel Sammet
 */
public class Session {
	private SessionContents contents;
	private Knowledgebase knowledgebase;

	public Session(Knowledgebase kb) {
		contents = new SessionContents();
		knowledgebase = kb;
	}

	/**
	 * This method is only to be used by the Knowledbase.
	 * 
	 * @return
	 */
	SessionContents getContents() {
		return contents;
	}

	/**
	 * Sets an attribute to the given value.
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value) {
		knowledgebase.setAttribute(getContents(), name, value);
	}

	/**
	 * Returns the value(s) of an attribute.
	 * 
	 * @param name
	 * @return Returns a String for TextAttributes or an array of EnumElements for
	 *         Enumerations
	 */
	public Object[] getValueContents(String name) {
		return knowledgebase.getAttribute(name).getAllValues(contents);
	}

	public String[] getSelectedValues(String name) {
		return knowledgebase.getAttribute(name).getSelectedValuesAsStrings(contents);
	}
	

	/**
	 * Clears the session. All informations about selections or status of
	 * attributes or rules are lost.
	 */
	public void clear() {
		knowledgebase.clear(getContents());
	}

	// TODO Probably these method can stay in the Knowledgebase
	/**
	 * @return
	 */
	public Collection<AbstractAttribute> getAllAttributes() {
		return knowledgebase.getAllAttributes();
	}

	/**
	 * @return Returns the sequenceId.
	 */
	public int getNextSequenceId() {
		return contents.getNextSequenceId();
	}

}
