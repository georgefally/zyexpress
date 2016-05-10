package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import net.zyexpress.site.api.Package;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.dao.PackageDAO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/package")
@Produces(MediaType.APPLICATION_JSON)
public class PackageResource {

    private final PackageDAO packageDAO;

    public PackageResource(PackageDAO packageDAO) {
        this.packageDAO = packageDAO;
    }

    // TODO: pass json object array instead of many form fields
    @Path("/add")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addNewPackage(@FormParam("package_account_name") @NotEmpty String packageAccountName,
                                  @FormParam("package_weight") Double packageWeight,
                                  @FormParam("package_item1_name") @NotEmpty String packageItem1Name,
                                  @FormParam("package_item1_brand") @NotEmpty String packageItem1Brand,
                                  @FormParam("package_item1_specification") String packageItem1Specification,
                                  @FormParam("package_item1_quantity") Integer packageItem1Quantity,
                                  @FormParam("package_item2_name") @NotEmpty String packageItem2Name,
                                  @FormParam("package_item2_brand") @NotEmpty String packageItem2Brand,
                                  @FormParam("package_item2_specification") String packageItem2Specification,
                                  @FormParam("package_item2_quantity") Integer packageItem2Quantity) {
        try {
            Package pkg = new Package(packageAccountName, packageWeight);
            pkg.addItem(packageItem1Name, packageItem1Brand, packageItem1Specification, packageItem1Quantity);
            pkg.addItem(packageItem2Name, packageItem2Brand, packageItem2Specification, packageItem2Quantity);

            long packageId = packageDAO.addPackage(pkg);
            for (Package.PackageItem packageItem : pkg.getItems()) {
                packageDAO.addPackageItem(packageId, packageItem);
            }

            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, packageId);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }
    }

}