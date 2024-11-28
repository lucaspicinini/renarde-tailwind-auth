package util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkiverse.renarde.security.RenardeSecurity;
import io.quarkiverse.renarde.security.RenardeUser;
import io.quarkiverse.renarde.security.RenardeUserProvider;
import model.User;

@ApplicationScoped
public class SecuritySetup implements RenardeUserProvider {

  @Inject
  RenardeSecurity security;

  @Override
  public RenardeUser findUser(String tenantId, String id) {
    return User.findByUserName(id);
  }
}
