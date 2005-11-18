package de.scp.selector.ruleengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.scp.selector.util.PublicCloneable;

/**
 * A SessionContents is a container for all stateful information of attributes
 * <li>selections</li>
 * <li>status ...</li>
 * <br>
 * and rules
 * <li>has fired ...</li>
 * <br>
 * Furthermore SessionContents holds the rollback information and functionality.
 * Session is not yet implemented or used, but should be reflected in all
 * signatures of methods (when needed in the future).
 * 
 * @author Axel Sammet
 */
// TODO implement usage of session in Enumeration
public class SessionContents {
	private int sequenceId = 2;
	private Map<String, PublicCloneable> contents = new HashMap<String, PublicCloneable>();
	private Map<String, PublicCloneable> rollbackContents = new HashMap<String, PublicCloneable>();

	/**
	 * Constructor must have package visibility to allow Session the creation
	 * and forbid creation outside of this framework.
	 */
	protected SessionContents() {
	}

	/**
	 * Returns the next number of the internal session sequence.
	 * 
	 * @return
	 */
	public int getNextSequenceId() {
		return sequenceId++;
	}

	/**
	 * Gets contents for a given key.
	 * 
	 * @param key
	 * @return
	 */
	public Object getContents(String key) {
		return contents.get(key);
	}

	/**
	 * Puts contents into the SessionContents. If necessary the existing
	 * contents is saved for a later rollback.
	 * 
	 * @param key
	 * @param obj
	 */
	public void putContents(String key, PublicCloneable obj) {
		// first save old content for rollback (if there is not already one)
		if (!rollbackContents.containsKey(key)) {
			PublicCloneable clone = obj.clone();
			rollbackContents.put(key, clone);
		}
		contents.put(key, obj);
	}

	/**
	 * Rolls the changes since the last call to
	 * <code>clearRollbackSegment()</code> back.
	 */
	public void rollback() {
		for (Iterator<String> iter = rollbackContents.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			contents.put(key, rollbackContents.get(key));
		}
	}

	/**
	 * Clears the rollback segment.
	 */
	public void clearRollbackSegment() {
		rollbackContents.clear();
	}
}
