package de.scp.selector.ruleengine;

import java.util.List;

import de.scp.selector.ruleengine.attributes.AbstractAttribute;


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
		kb.clear();		
	}

	/**
	 * @return
	 */
	public List<AbstractAttribute> getAllAttributes() {
		return kb.getAllAttributes();
	}
}
