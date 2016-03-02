package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.dao.UserIdCardDAO;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    //@GET
    @POST
    @Timed
    @Path("/downloadId")
    @Produces("application/zip")
    public StreamingOutput downloadIdCardFile(@FormParam("memIdGroup") String memIdStr) {
        //public StreamingOutput downloadIdCardFile(@QueryParam("memIdGroup") List<String> memIdList) {
        List<String> memIdList = Splitter.on("&").omitEmptyStrings().splitToList(memIdStr);
        List<String> memIdListNew = new ArrayList<String>();
        for (String item : memIdList) {
            memIdListNew.add(item.substring(11));
        }
        final List<UserIdCard> userIdCards = userIdCardDAO.findByUserIds(memIdListNew);
        return output -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(output)) {
                for (UserIdCard userIdCard : userIdCards) {
                    // each userId has a dedicated directory
                    File userDirectory = Paths.get(uploadDir, userIdCard.getIdNumber()).toFile();
                    File[] files = userDirectory.listFiles();
                    if (files == null) {
                        throw new RuntimeException("empty directory: " + userDirectory.getAbsolutePath());
                    }
                    for (File file : files) {
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

    @POST
    @Timed
    @Path("/uploadExcel")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadExcel(@FormDataParam("excelfile") final InputStream uploadFileStream,
                                @FormDataParam("excelfile") final FormDataContentDisposition contentDisposition) throws IOException {

        File tempFile = File.createTempFile("zyexpress", "zip");
        OutputStream outputStream = new FileOutputStream(tempFile);
        ByteStreams.copy(uploadFileStream, outputStream);


        String output = "SUCCESS";
        return Response.status(200).entity(output).build();
    }
}
