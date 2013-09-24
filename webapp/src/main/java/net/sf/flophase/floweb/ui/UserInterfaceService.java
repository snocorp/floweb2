package net.sf.flophase.floweb.ui;

import java.io.Writer;

/**
 * This service provides functionality for the user interface.
 */
public interface UserInterfaceService {
	/**
	 * Writes the splash page to the given writer.
	 * 
	 * @param destinationURL
	 *            The destination URL after login
	 * @param writer
	 *            The writer for output
	 */
	public void writeSplash(String destinationURL, Writer writer);

	/**
	 * Writes the application page to the given writer.
	 * 
	 * @param destinationURL
	 *            The destination after logout
	 * @param writer
	 *            The writer
	 */
	public void writeApp(String destinationURL, Writer writer);
}
