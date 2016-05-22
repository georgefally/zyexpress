package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.Package;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.dao.PackageDAO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Path("/package")
@Produces(MediaType.APPLICATION_JSON)
public class PackageResource {
    private static final Logger logger = LoggerFactory.getLogger(PackageResource.class);

    private final DBI jdbi;
    private final PackageDAO packageDAO;

    public PackageResource(DBI jdbi, PackageDAO packageDAO) {
        this.jdbi = jdbi;
        this.packageDAO = packageDAO;
    }

    @POST
    @Timed
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadExcel(@FormDataParam("packageExcelFile") final InputStream uploadFileStream,
                                @FormDataParam("packageExcelFile") final FormDataContentDisposition contentDisposition) throws IOException {
        String fileName = contentDisposition.getFileName();
        Workbook workbook;
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(uploadFileStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(uploadFileStream);
        } else {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Invalid file type, only xlsx or xls are allowed.");
            return Response.status(403).entity(response).build();
        }

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.rowIterator();

        boolean headerSkipped = false;
        List<Package> packages = Lists.newLinkedList();
        Package pkg = null;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            if (row.getCell(0) != null) {
                if (pkg != null) packages.add(pkg);
                String accountName = row.getCell(1).getStringCellValue();
                double weight = row.getCell(2).getNumericCellValue();
                pkg = new Package(accountName, weight);
            }
            String itemBrand = row.getCell(3).getStringCellValue();
            String itemName = row.getCell(4).getStringCellValue();
            String itemSpecification = row.getCell(5).getStringCellValue();
            int itemQuantity = (int)row.getCell(6).getNumericCellValue();
            pkg.addItem(itemName, itemBrand, itemSpecification, itemQuantity);
        }
        packages.add(pkg);

        try {
            List<Package> savedPackages = Lists.newLinkedList();
            for (Package p : packages) {
                long packageId = packageDAO.addPackage(p);
                for (Package.PackageItem packageItem : p.getPackageItems()) {
                    packageDAO.addPackageItem(packageId, packageItem);
                }
                savedPackages.add(p);
            }
            logger.info("In total {} new packages added: {}. ", savedPackages.size(), savedPackages.toString());

            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, savedPackages);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }
    }

    @Path("/search")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response searchPackages(@Auth AuthPrincipal principal,
                                   @FormParam("search_user_name") final String searchUserName) {
        try {
            if (principal == null) {
                RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                        "Not authorized - have you logged in?");
                return Response.status(403).entity(response).build();
            }
            if (Strings.isNullOrEmpty(searchUserName)) {
                RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                        "No search user provided.");
                return Response.status(403).entity(response).build();
            }
            List<Integer> packageIds = packageDAO.searchPackages(searchUserName);
            List<Package> packages = Lists.newLinkedList();
            for (Integer packageId : packageIds) {
                Package pkg = packageDAO.searchPackageDetail(packageId);
                List<Package.PackageItem> items = packageDAO.searchPackageItems(packageId);
                pkg.addItems(items);
                packages.add(pkg);
            }
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, packages);
            logger.info("In total {} packages found for user {}: {}.", packages.size(), searchUserName,
                    packages.toString());
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            logger.error("Failed to query package for " + searchUserName, ex);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }

    }
}
