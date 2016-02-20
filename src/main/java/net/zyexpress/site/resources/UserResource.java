package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import net.zyexpress.site.api.User;
import net.zyexpress.site.dao.UserDAO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Timed
    public User getUser(@QueryParam("name") @NotEmpty String name) {
        return userDAO.findByName(name);
    }

    @PUT
    @Timed
    @Path("/all")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User addUser(@FormParam("name") String name, @FormParam("password") String password) {
        userDAO.insert(new User(name, password));
        return userDAO.findByName(name);
    }

    @GET
    @Timed
    @Path("/all")
    public List<User> getAllUser() {
        return userDAO.getAll();
    }
}