package de.scp.selector.ruleengine;

import java.util.List;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;

/**
 * A Session is a container for all stateful information of attributes
 * <li>selections</li>
 * <li>status ...</li><br>
 * and rules
 * <li>has fired ...</li>
 * <br>
 * Session is not yet implemented or used, but should be reflected in all
 * signatures of methods (when needed in the future).
 * 
 * @author Axel Sammet
 */
// TODO implement usage of session in Enumeration
public class Session {
	private Knowledgebase kb;

	public Session(Knowledgebase kb) {
		this.kb = kb;
	}

	public void setAttribute(String name, String value) {
		kb.setAttribute(this, name, value);
	}

	/**
	 * 
	 */
	public void clear() {
		kb.clear(this);
	}

	/**
	 * @return
	 */
	public List<AbstractAttribute> getAllAttributes() {
		return kb.getAllAttributes();
	}
}
