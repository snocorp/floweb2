package net.sf.flophase.floweb.test;

import com.google.inject.Provider;

/**
 * This class is a basic provider that always returns the same value.
 * 
 * @param <T>
 *            The type of value
 */
public class MockProvider<T> implements Provider<T> {

	/**
	 * The value to be provided.
	 */
	private final T value;

	/**
	 * Creates a MockProviderInstance
	 * 
	 * @param value
	 *            The value to be provided.
	 */
	public MockProvider(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

}
