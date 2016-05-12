package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import net.zyexpress.site.api.Package;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.dao.PackageDAO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Path("/package")
@Produces(MediaType.APPLICATION_JSON)
public class PackageResource {

    private final PackageDAO packageDAO;

    public PackageResource(PackageDAO packageDAO) {
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
                for (Package.PackageItem packageItem : p.getItems()) {
                    packageDAO.addPackageItem(packageId, packageItem);
                }
                savedPackages.add(p);
            }

            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, savedPackages);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }
    }
}
