package de.scp.selector.ruleengine;

import java.util.List;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;

public class Session {
	private SessionContents contents;
	private Knowledgebase knowledgebase;
	
	public Session(Knowledgebase kb) {
		contents = new SessionContents();
		knowledgebase = kb;
	}
	
	/**
	 * This method is only to be used by the Knowledbase.
	 * @return
	 */
	SessionContents getContents() {
		return contents;
	}
	
	/**
	 * Sets an attribute to the given value.
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value) {
		knowledgebase.setAttribute(getContents(), name, value);
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
	public List<AbstractAttribute> getAllAttributes() {
		return knowledgebase.getAllAttributes();
	}

	/**
	 * @return Returns the sequenceId.
	 */
	public int getNextSequenceId() {
		return contents.getNextSequenceId();
	}


}
