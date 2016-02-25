package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.ByteStreams;
import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.dao.UserIdCardDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        return userIdCardDAO.getAll();
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

    @GET
    @Timed
    @Path("/export")
    @Produces("application/zip")
    public StreamingOutput downloadFile(@Context HttpServletRequest request, @Context HttpServletResponse response,
                                 @QueryParam("ids") List<String> userIds) {
        return output -> {
            try (InputStream inputStream1 = new FileInputStream("/var/tmp/image1.png");
                 InputStream inputStream2 = new FileInputStream("/var/tmp/image2.png");
                 ZipOutputStream zipOutputStream = new ZipOutputStream(output)) {
                zipOutputStream.putNextEntry(new ZipEntry("image1.png"));
                ByteStreams.copy(inputStream1, zipOutputStream);
                zipOutputStream.putNextEntry(new ZipEntry("image2.png"));
                ByteStreams.copy(inputStream2, zipOutputStream);
            }
        };
    }
}
