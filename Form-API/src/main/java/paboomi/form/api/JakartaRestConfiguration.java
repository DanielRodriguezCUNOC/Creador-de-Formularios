package paboomi.form.api;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * 
 * @author Juneau
 */
@ApplicationPath("api/v1")
public class JakartaRestConfiguration extends ResourceConfig {
  /**
   * Registers the resource classes and providers for the REST API.
   */
  public JakartaRestConfiguration() {
    // Register resource classes
    packages("paboomi.form.api.resources");

    // Register providers (e.g., JSON serialization)
    register(MultiPartFeature.class);
  }

}
