package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.dao.UserIdCardDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/userIdCard")
@Produces(MediaType.APPLICATION_JSON)
public class UserIdCardResource {

    private final UserIdCardDAO userIdCardDAO;

    public UserIdCardResource(UserIdCardDAO userIdCardDAO) {
        this.userIdCardDAO = userIdCardDAO;
    }

    @GET
    @Timed
    @Path("/all")
    public List<UserIdCard> getAllUser() {
        List a = userIdCardDAO.getAll();
        return a;
    }

    @GET
    @Timed
    @Path("/search")
    public List<UserIdCard> getUser(@QueryParam("cname") String userName) {
        if (userName.isEmpty()) {
            return userIdCardDAO.getAll();
        } else {
            return userIdCardDAO.findByUserName("%" + userName + "%");
        }

    }

   /* @GET
    @Timed
    @Path("/export")
    public void exportFile(@Context HttpServletRequest request, @Context HttpServletResponse response,
                           @QueryParam("fileName")String fileName, @QueryParam("objectName")String objectName,
                           @QueryParam("docbelong")String docbelong) throws UnsupportedEncodingException {
        //获得请求文件名
        System.out.println(fileName);
    }*/
}
