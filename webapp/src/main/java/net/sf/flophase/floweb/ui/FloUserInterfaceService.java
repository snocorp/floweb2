package net.sf.flophase.floweb.ui;

import java.io.Writer;

import javax.inject.Inject;

import net.sf.flophase.floweb.user.UserStore;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.appengine.api.users.User;

/**
 * This service provides functionality for the user interface.
 */
public class FloUserInterfaceService implements UserInterfaceService {

	private static final String APP_MODE = "net.sf.flophase.floweb.ui.app.mode";

	private static final String DEFAULT_APP_MODE = "std";

	@Inject
	private UserStore userStore;

	@Inject
	private VelocityEngine velocity;

	@Override
	public void writeSplash(String destinationURL, Writer writer) {
		Template t = velocity
				.getTemplate("net/sf/flophase/floweb/ui/splash.html");

		VelocityContext context = new VelocityContext();
		context.put("loginUrl", userStore.createLoginURL(destinationURL));

		t.merge(context, writer);
	}

	@Override
	public void writeApp(final String destinationURL, final Writer writer) {
		final User user = userStore.getUser();
		if (user != null) {
			String appMode = userStore.getSetting(APP_MODE);

			String appTemplate = "net/sf/flophase/floweb/ui/" + appMode
					+ "/app.html";
			if (!velocity.resourceExists(appTemplate)) {
				appMode = DEFAULT_APP_MODE;
				appTemplate = "net/sf/flophase/floweb/ui/" + appMode
						+ "/app.html";
			}

			final Template t = velocity.getTemplate(appTemplate);

			final VelocityContext context = new VelocityContext();

			context.put("userEmail", user.getEmail());
			context.put("logoutUrl", userStore.createLogoutURL(destinationURL));

			t.merge(context, writer);
		} else {
			writeSplash("/", writer);
		}
	}

}
