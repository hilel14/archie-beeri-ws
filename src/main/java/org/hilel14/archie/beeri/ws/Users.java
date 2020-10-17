package org.hilel14.archie.beeri.ws;

import java.security.Key;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.hilel14.archie.beeri.core.Config;

import org.hilel14.archie.beeri.core.users.Credentials;
import org.hilel14.archie.beeri.core.users.User;
import org.hilel14.archie.beeri.core.users.UserManager;

/**
 * Root resource (exposed at "users" path)
 *
 * @author hilel14
 */
@Path("users")
public class Users {

    static final Logger LOGGER = LoggerFactory.getLogger(Users.class);
    final Config config;
    final UserManager userManager;

    public Users(@Context Configuration configuration) throws Exception {
        this.config = (Config) configuration.getProperty("archie.config");
        Key key = (Key) configuration.getProperty("jwt.key");
        userManager = new UserManager(config, key);
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() throws SQLException {
        return userManager.getAllUsers();
    }

    @POST
    @Path("authenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User authenticate(Credentials credentials) throws Exception {
        LOGGER.debug("authenticating user {}", credentials.getUsername());
        User user = userManager.authenticate(credentials);
        LOGGER.info("authentication {} for user {}",
                user == null ? "failed" : "succeeded", credentials.getUsername());
        return user;
    }

    @POST
    @Path("authenticate-with-google")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User authenticateWithGoogle(Map<String, Object> socialUser) throws Exception {
        LOGGER.debug("authenticating user {}", socialUser.get("email"));
        User user = userManager.authenticateWithGoogle(socialUser);
        LOGGER.debug("authentication {} for google user {}",
                user == null ? "failed" : "succeeded", socialUser.get("email"));
        return user;
    }

    @POST
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(User user) throws Exception {
        userManager.createUser(user);
    }

    @PUT
    @Path("password")
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePassword(User user) throws Exception {
        LOGGER.info("changing password of user {} ", user.getUsername());
        userManager.updatePassword(user);
    }

    @PUT
    @Path("role")
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(User user) throws Exception {
        LOGGER.info("changing role of user {} ", user.getUsername());
        userManager.updateRole(user);
    }

    @DELETE
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(User user) throws Exception {
        LOGGER.info("deleting user {} ", user.getUsername());
        userManager.deleteUser(user);
    }

}
