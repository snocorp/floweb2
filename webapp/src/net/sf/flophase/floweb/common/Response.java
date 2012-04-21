package net.sf.flophase.floweb.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The Response class is a generic way that service classes can respond to invocations.
 * 
 * @param <T>
 *            The type of parameter the response will hold.
 */
public class Response<T> {
	/**
	 * Indicates failure of the invocation.
	 */
	public static final int RESULT_FAILURE = 0;

	/**
	 * Indicates success of the invocation.
	 */
	public static final int RESULT_SUCCESS = 1;

	/**
	 * The result of the invocation.
	 */
	private int result;

	/**
	 * Any messages that resulted during the logic.
	 */
	private List<String> messages = new ArrayList<String>();

	/**
	 * The content of the response.
	 */
	private T content;

	/**
	 * Creates a new empty response.
	 * 
	 * @param result
	 *            The result
	 */
	public Response(int result) {
		this(result, null);
	}

	/**
	 * Creates a new {@link Response} with the given content.
	 * 
	 * @param result
	 *            The result
	 * @param content
	 *            The content
	 */
	public Response(int result, T content) {
		this.result = result;
		this.content = content;
	}

	/**
	 * Adds a message to the response.
	 * 
	 * @param message
	 *            The message
	 */
	public void addMessage(String message) {
		messages.add(message);
	}

	/**
	 * Returns the result of the response.
	 * 
	 * @return The result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * Returns the list of messages in the response.
	 * 
	 * @return The messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Returns the content of the response.
	 * 
	 * @return The content
	 */
	public T getContent() {
		return content;
	}

	/**
	 * Sets the results of the response.
	 * 
	 * @param result
	 *            The result (success or failure)
	 */
	public void setResult(int result) {
		this.result = result;
	}

	/**
	 * Sets the content of the response.
	 * 
	 * @param content
	 *            The content
	 */
	public void setContent(T content) {
		this.content = content;
	}
}
