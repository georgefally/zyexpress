package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.api.User;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.auth.TokenBasedAuthenticator;
import net.zyexpress.site.dao.UserDAO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Path("login")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("login_username") @NotEmpty String userName,
                          @FormParam("login_password") @NotEmpty String password) {
        User user = userDAO.findByUserName(userName);
        if (user == null) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Invalid user name or password");
            return Response.status(403).entity(response).build();
        }
        if (!Objects.equal(user.getPassword(), password)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Invalid user name or password");
            return Response.status(403).entity(response).build();
        }

        String token = TokenBasedAuthenticator.getUniqueToken();
        AuthPrincipal principal = new AuthPrincipal(user.getUserName(), token, user.getIsAdmin());
        TokenBasedAuthenticator.addPrincipalToCache(token, principal);

        RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, token);
        return Response.status(200).entity(response).build();
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerUser(@FormParam("register_username") @NotEmpty String userName,
                                 @FormParam("register_password") @NotEmpty String password) {
        try {
            User existingUser = userDAO.findByUserName(userName);
            if (existingUser != null) {
                String msg = String.format("User name %s already exists", userName);
                RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, msg);
                return Response.status(500).entity(response).build();
            }
            User user = new User(userName, password, false, false);
            userDAO.insert(user);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, user);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }
    }

    @Path("query")
    @POST
    @Timed
    public Response getUser(@Auth AuthPrincipal principal,
                            @FormParam("query_username") @NotEmpty String userName) {
        if (!Objects.equal(principal.getName(), userName)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }
        try {
            User user = userDAO.findByUserName(userName);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, user);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }

    @Path("password")
    @POST
    @Timed
    public Response updatePassword(@Auth AuthPrincipal principal,
                                   @FormParam("modify_password_new_password") @NotEmpty String newPassword) {
        if (principal == null) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }
        try {
            userDAO.updatePassword(principal.getName(), newPassword);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, principal.getName());
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }

    @DELETE
    @Timed
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public int deleteUser(@Auth AuthPrincipal principal, @FormParam("userName") String userName) {
        return userDAO.deleteByUserName(userName);
    }
}