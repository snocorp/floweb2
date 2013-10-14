package net.sf.flophase.floweb.common;

/**
 * This interface specifies something that can be executed and returns a
 * response.
 * 
 * @param <T>
 *            The type that will be returned.
 */
public interface Executable<T> {
	/**
	 * Executes some logic.
	 * 
	 * @return The result
	 */
	public abstract T execute();
}
