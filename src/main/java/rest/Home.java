package rest;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Home Controller
 */
@Path("/")
public class Home extends Controller {

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance index();
  }

  @GET
  @Path("/")
  public TemplateInstance index() {
    return Templates.index();
  }
}
