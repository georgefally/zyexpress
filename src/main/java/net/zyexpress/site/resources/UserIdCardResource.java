package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import net.zyexpress.site.util.ExcelGenerator;
import net.zyexpress.site.util.ExcelReader;
import net.zyexpress.site.util.UnZip;
import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.dao.UserIdCardDAO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/userIdCard")
@Produces(MediaType.APPLICATION_JSON)
public class UserIdCardResource {
    private static final Logger logger = LoggerFactory.getLogger(UserIdCardResource.class);

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
                    File userDirectory = Paths.get(uploadDir+"uploadidcard/", userIdCard.getIdNumber()).toFile();
                    File[] files = userDirectory.listFiles();
                    if (files == null) {
                        logger.error("empty directory: " + userDirectory.getAbsolutePath());
                        continue;
                        //throw new RuntimeException("empty directory: " + userDirectory.getAbsolutePath());
                    }
                    for (File file : files) {
                        if (file.isDirectory()) continue;
                        try (InputStream inputStream = new FileInputStream(file)) {
                            zipOutputStream.putNextEntry(new ZipEntry( file.getName()));
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadExcel(@FormDataParam("excelfile") final InputStream uploadFileStream,
                                @FormDataParam("excelfile") final FormDataContentDisposition contentDisposition) throws IOException {
        String fileExtension = Files.getFileExtension(contentDisposition.getFileName());
        File tempFile = File.createTempFile("zyexpress", fileExtension);
        OutputStream outputStream = new FileOutputStream(tempFile);
        ByteStreams.copy(uploadFileStream, outputStream);
        Map<String, String> response = Maps.newHashMap();
        Integer responseStatus = 200;
        try {
            processUploadedExcelFile(tempFile);
            // $output = ['uploaded' => $paths];
            response.put("uploaded", contentDisposition.getFileName());
            return Response.status(responseStatus).entity(response).build();
        }catch (Exception ex){
            logger.error("Failed to process uploaded excel file. ", ex);
            return Response.status(responseStatus).entity(ex).build();
        }
    }

    private void processUploadedExcelFile(File file) throws IOException {
        String filePath = file.getPath();
        Workbook workbook = null;
        if(filePath.trim().toLowerCase().endsWith("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(filePath)) ;
        } else if(filePath.trim().toLowerCase().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(filePath)) ;
        } else {
            throw new IllegalArgumentException("上传文件以xlsx结尾!!!") ;
        }

        int sheetTotal = workbook.getNumberOfSheets() ;
        Sheet sheet = ExcelReader.getSheet(workbook, 0) ;
        List<Object[]> list = ExcelReader.listFromSheet(sheet) ;
        // process file
        int recordNumbeer = 0;

        for(Object[] obj : list){
            if(obj[0].toString().trim().equals("")&&obj[1].toString().trim().equals(""))continue;
            System.out.println(Arrays.asList(obj));
            logger.info("["+obj[1].toString().trim()+","+obj[0].toString().trim()+"]");
            //System.out.println(obj[1].toString().trim());
            List userId = userIdCardDAO.findByUserId(obj[1].toString().trim());
            if(!userId.isEmpty()){
                userIdCardDAO.deleteByUserId(obj[1].toString().trim());
                //statement.executeUpdate("delete from userIdCardNew where idNumber= '"+obj[1].toString().trim()+"'");
            }
            userIdCardDAO.insert(obj[1].toString().trim(),obj[0].toString().trim());
            recordNumbeer++;
            //statement.executeUpdate("insert into userIdCardNew values('"+obj[1].toString().trim()+"','"+obj[0].toString().trim()+"')");
        }

        System.out.println("insert into UserIdCardResource "+recordNumbeer+" records");
        logger.info("insert into UserIdCardResource "+recordNumbeer+" records");
    }

    @POST
    @Timed
    @Path("/uploadPicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPicture(@FormDataParam("picfile") final InputStream uploadFileStream,
                                @FormDataParam("picfile") final FormDataContentDisposition contentDisposition) throws IOException {
        String fileExtension = Files.getFileExtension(contentDisposition.getFileName());
        File tempFile = File.createTempFile("zyexpress", fileExtension);
        OutputStream outputStream = new FileOutputStream(tempFile);
        ByteStreams.copy(uploadFileStream, outputStream);
        Map<String, String> response = Maps.newHashMap();
        Integer responseStatus = 200;
        try {
            UnZip unzp = new UnZip();
            File userDirectory = Paths.get(uploadDir).toFile();
            unzp.unzip(tempFile,userDirectory );
            //processUploadedExcelFile(tempFile);
            // $output = ['uploaded' => $paths];
            response.put("uploaded", contentDisposition.getFileName());
            return Response.status(responseStatus).entity(response).build();
        }catch (Exception ex){
            System.out.println(UserIdCardResource.class.getName()+ ex);
            logger.error(UserIdCardResource.class.getName()+ ex);
            return Response.status(responseStatus).entity(ex).build();
        }
    }


    //@GET
    @POST
    @Timed
    @Path("/exportId")
    @Produces("application/vnd.ms-excel;charset=utf-8")
    public Response exportMemberIdCard(@FormParam("memIdGroup") String memIdStr) {
        //public StreamingOutput downloadIdCardFile(@QueryParam("memIdGroup") List<String> memIdList) {
        List<String> memIdList = Splitter.on("&").omitEmptyStrings().splitToList(memIdStr);
        List<String> memIdListNew = new ArrayList<String>();
        for (String item : memIdList) {
            memIdListNew.add(item.substring(11));
        }
        final List<UserIdCard> userIdCardsList = userIdCardDAO.findByUserIds(memIdListNew);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("curs",userIdCardsList);

        ContentDisposition contentDisposition = ContentDisposition.type("attachment")
                .fileName("filename.xls").creationDate(new Date()).build();
        StreamingOutput streamingOutput = output -> ExcelGenerator.createExcel("excel_memIdcard.ftl", output, map, "GBK");
        return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).build();
    }
}
