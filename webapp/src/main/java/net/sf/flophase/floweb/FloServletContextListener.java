package net.sf.flophase.floweb;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Create the injector with the required modules.
 */
public class FloServletContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new FloModule(), new FloServletModule());
	}

}
