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
    public User getUser(@QueryParam("userName") @NotEmpty String userName) {
        return userDAO.findByUserName(userName);
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User addUser(@FormParam("userName") String userName, @FormParam("password") String password) {
        userDAO.insert(new User(userName, password));
        //return userDAO.findByUserName(userName);
        return new User(userName, password);
    }

    @DELETE
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public int deleteUser(@FormParam("userName") String userName) {
        return userDAO.deleteByUserName(userName);
    }

    @GET
    @Timed
    @Path("/all")
    public List<User> getAllUser() {
        return userDAO.getAll();
    }
}