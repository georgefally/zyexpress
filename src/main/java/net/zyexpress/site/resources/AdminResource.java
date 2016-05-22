package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.api.User;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.dao.UserDAO;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lumengyu on 2016/5/9.
 * APIs used by admin user.
 */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private static final Logger logger = LoggerFactory.getLogger(UserIdCardResource.class);
    private final UserDAO userDAO;
    private final String uploadDir;
    private final DBI jdbi;

    public  AdminResource(UserDAO userDAO, String uploadDir, DBI jdbi) {
        this.userDAO = userDAO;
        this.uploadDir = uploadDir;
        this.jdbi = jdbi;
    }

    //@GET 下载订单上传的模板文件
    @POST
    @Timed
    @Path("/downloadModel")
    @Produces("application/zip")
    public StreamingOutput downloadIdCardFile(@FormParam("memIdGroup") String memIdStr) {
        return output -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(output)) {
                File userDirectory = Paths.get(uploadDir+"uploadidcard/","model").toFile() ;
                File[] files = userDirectory.listFiles();
                if (files == null) {
                    logger.error("empty directory: " + userDirectory.getAbsolutePath());
                    //throw new RuntimeException("empty directory: " + userDirectory.getAbsolutePath());
                }
                for (File file : files) {
                    if (file.isDirectory()) continue;
                    try (InputStream inputStream = new FileInputStream(file)) {
                        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                        ByteStreams.copy(inputStream, zipOutputStream);
                    }
                }
            }
        };
    }

    @GET
    @Timed
    @Path("/getUserId")
    public List<User> getUser(@QueryParam("cname") String userName,@QueryParam("userStatus") String userStatus) {
        if (Strings.isNullOrEmpty(userName)) {
            return userDAO.getAdminAll(userStatus);
        }
        return userDAO.getAdminAll(userStatus);
       // List<String> userNames = Splitter.on(" ").omitEmptyStrings().splitToList(userName);
       // return userDAO.findByUserName(userName);
    }

    @POST
    @Timed
    @Path("/updateUserStatus")
    public Response updateUserStatus(@FormParam("memIdGroup") String memIdStr) {
        try {
            List<String> memIdList = Splitter.on("&").omitEmptyStrings().splitToList(memIdStr);
            List<String> memIdListNew = new ArrayList<String>();
            for (String item : memIdList) {
                userDAO.updateStatus(item);
            }
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS,"success");
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }

    @POST
    @Timed
    @Path("execute_sql")
    @RolesAllowed("ADMIN")
    public Response executeSQL(@Auth AuthPrincipal principal, @FormParam("sql") String sql) {
        sql = sql.toLowerCase();
        Handle handle = jdbi.open();

        RestfulResponse response;
        if (sql.startsWith("select")) {
            List<Map<String, Object>> result = handle.select(sql);
            response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, result);
        } else {
            handle.execute(sql);
            response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, sql);
        }

        return Response.status(200).entity(response).build();
    }
}
