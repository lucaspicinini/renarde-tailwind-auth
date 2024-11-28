package rest;

import io.quarkiverse.renarde.router.Router;
import io.quarkiverse.renarde.security.ControllerWithUser;
import io.quarkiverse.renarde.security.RenardeSecurity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import model.User;
import model.UserStatus;
import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;

@Path("/")
public class Auth extends ControllerWithUser<User> {
  @Inject
  RenardeSecurity security;

  @CheckedTemplate
  static class Templates {
    public static native TemplateInstance loginPage();
    public static native TemplateInstance registerPage();
    public static native TemplateInstance logoutPage();
    public static native TemplateInstance dashboard();
  }

  /**
   * Welcome page at the end of registration
   */
  @Authenticated
  @GET
  @Path("/dashboard")
  public TemplateInstance dashboard() {
    return Templates.dashboard();
  }

  /**
   * Login Form Page
   */
  @GET
  @Path("/login-page")
  public TemplateInstance loginPage() {
    return Templates.loginPage();
  }

  /**
   * Login Form Action
   */
  @POST
  @Path("/login")
  public Response login(
    @RestForm @NotBlank @Length(max = 50) String userName,
    @RestForm @Length(min = 8) String password
  ) {
    User user = User.findRegisteredByUserName(userName);

    validation.required("userName", userName);
    validation.required("password", password);

    if (user == null || !BcryptUtil.matches(password, user.password)) {
      validation.addError("userName", "Invalid username/pasword");
      prepareForErrorRedirect();
      loginPage();
    }

    NewCookie cookie = security.makeUserCookie(user);

    return Response.seeOther(Router.getURI(Auth::dashboard))
      .cookie(cookie)
      .build();
  }

  @GET
  @Path("/register-page")
  public TemplateInstance registerPage() {
    checkLogout();
    return Templates.registerPage();
  }

  @POST
  @Path("/register")
  public Response register(
    @RestForm @Email @NotBlank @Length(max = 255) String email,
    @RestForm @NotBlank @Length(max = 50) String userName,
    @RestForm @NotBlank @Length(max = 50) String firstName,
    @RestForm @Length(max = 50) String lastName,
    @RestForm @NotBlank @Length(min = 11, max = 11) String phone,
    @RestForm @Length(min = 8) String password,
    @RestForm @Length(min = 8) String password2
  ) {
    checkLogout();
    validation.required("email", email);
    validation.required("userName", userName);
    validation.required("firstName", firstName);
    validation.required("phone", phone);
    validation.required("password", password);
    validation.required("password2", password2);
    validation.equals("password", password, password2);

    if (User.findRegisteredByEmail(email) != null)
      validation.addError("email", "Email already in use");

    if (User.findRegisteredByUserName(userName) != null)
      validation.addError("userName", "Username already taken");

    if (User.findRegisteredByPhone(phone) != null)
      validation.addError("phone", "Phone already in use");

    if (validationFailed()) registerPage();

    User user = new User();
    user.email = email.trim();
    user.userName = userName.trim();
    user.firstName = firstName.trim();
    user.lastName = lastName.trim();
    user.phone = phone;
    user.password = BcryptUtil.bcryptHash(password);
    user.status = UserStatus.COMPLETED;
    user.isAdmin = false;
    user.persist();

    Response.ResponseBuilder responseBuilder =
      Response.seeOther(Router.getURI(Auth::dashboard));
    NewCookie cookie = security.makeUserCookie(user);
    responseBuilder.cookie(cookie);

    return responseBuilder.build();
  }

  /**
   * Link to logout page
   */
  @Authenticated
  @GET
  @Path("/logout-page")
  public TemplateInstance logoutPage() {
    return Templates.logoutPage();
  }

  private void checkLogout() {
    if (getUser() != null) {
      flash("logoutFirst", "You have been logged out");
      logoutPage();
    }
  }
}
