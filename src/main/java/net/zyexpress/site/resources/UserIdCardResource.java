package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.dao.UserIdCardDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/userIdCard")
@Produces(MediaType.APPLICATION_JSON)
public class UserIdCardResource {

    private final UserIdCardDAO userIdCardDAO;
    private final String uploadDir;

    public UserIdCardResource(UserIdCardDAO userIdCardDAO, String uploadDir) {
        this.userIdCardDAO = userIdCardDAO;
        this.uploadDir = uploadDir;
    }

    @GET
    @Timed
    @Path("/all")
    public List<UserIdCard> getAllUser() {
        return userIdCardDAO.getAll();
    }

    @GET
    @Timed
    @Path("/getUserId")
    public List<UserIdCard> getUser(@QueryParam("cname") String userName) {
        if (Strings.isNullOrEmpty(userName)) {
            return userIdCardDAO.getAll();
        }
        List<String> userNames = Splitter.on(" ").omitEmptyStrings().splitToList(userName);
        return userIdCardDAO.findByUserName(userNames);
    }

    @GET
    @Timed
    @Path("/downloadId")
    @Produces("application/zip")
    public StreamingOutput downloadIdCardFile(@QueryParam("memIdGroup") List<String> memIdList) {
        final List<UserIdCard> userIdCards = userIdCardDAO.findByUserIds(memIdList);
        return output -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(output)) {
                for (UserIdCard userIdCard : userIdCards) {
                    // each userId has a dedicated directory
                    File userDirectory = Paths.get(uploadDir, userIdCard.getIdNumber()).toFile();
                    for (File file : userDirectory.listFiles()) {
                        if (file.isDirectory()) continue;
                        try (InputStream inputStream = new FileInputStream(file)) {
                            zipOutputStream.putNextEntry(new ZipEntry(userIdCard.getIdNumber() + "/" + file.getName()));
                            ByteStreams.copy(inputStream, zipOutputStream);
                        }
                    }
                }
            }
        };
    }
}
