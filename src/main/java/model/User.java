package model;

import java.util.HashSet;
import java.util.Set;

import io.quarkiverse.renarde.security.RenardeUser;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntity implements RenardeUser {

  @Column(nullable = false, unique = true)
  public String email;
  @Column(unique = true, nullable = false, length = 50)
  public String userName;
  @Column(nullable = false, length = 50)
  public String firstName;
  @Column(length = 50)
  public String lastName;
  @Column(unique = true, nullable = false, length = 11)
  public String phone;
  @Column(nullable = false)
  public String password;
  @Column(nullable = false)
  public boolean isAdmin;

  // @Column(unique = true)
  // public String confirmationCode;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  public UserStatus status;


  @Override
  public boolean registered() {
    return status == UserStatus.COMPLETED;
  }

  @Override
  public Set<String> roles() {
    Set<String> roles = new HashSet<>();
    if (isAdmin) roles.add("admin");
    return roles;
  }

  @Override
  public String userId() {
    return userName;
  }


  //
  // Helpers

  public static User findUnconfirmedByEmail(String email) {
    return find("LOWER(email) = ?1 AND status = ?2", email.toLowerCase(), UserStatus.CONFIRMATION_REQUIRED).firstResult();
  }

  public static User findIncompleteByEmail(String email) {
    return find("LOWER(email) = ?1 AND status = ?2", email.toLowerCase(),
      UserStatus.INCOMPLETE).firstResult();
  }

  public static User findRegisteredByEmail(String email) {
    return find("LOWER(email) = ?1 AND status = ?2", email.toLowerCase(),
      UserStatus.COMPLETED).firstResult();
  }

  public static User findRegisteredByUserName(String username) {
    return find("LOWER(userName) = ?1 AND status = ?2", username.toLowerCase(), UserStatus.COMPLETED).firstResult();
  }

  public static User findRegisteredByPhone(String phone) {
    return find("phone = ?1 AND status = ?2", phone, UserStatus.COMPLETED).firstResult();
  }

  public static User findByUserName(String username) {
    return find("LOWER(userName) = ?1", username.toLowerCase()).firstResult();
  }

  public static User findForContirmation(String confirmationCode) {
    return find("confirmationCode = ?1 AND status = ?2", confirmationCode, UserStatus.CONFIRMATION_REQUIRED).firstResult();
  }
}
