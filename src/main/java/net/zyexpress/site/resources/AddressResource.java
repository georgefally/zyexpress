package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.*;
import net.zyexpress.site.api.Package;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.dao.AddressDAO;
import net.zyexpress.site.dao.PackageDAO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/address")
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

    private final AddressDAO addressDAO;

    public AddressResource(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    // TODO: pass json object array instead of many form fields
    @Path("/add")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addNewAddress(@QueryParam("login_name") String accountName,
                                  @FormParam("receiver_name") @NotEmpty String receiverName,
                                  @FormParam("phone_number") @NotEmpty String phoneNumber,
                                  @FormParam("postcode") String postcode,
                                  @FormParam("is_default") String isDefaultSet,
                                  @FormParam("provience") String province,
                                  @FormParam("city") String city,
                                  @FormParam("area") String area,
                                  @FormParam("address") String street) {
        Boolean isDefault = false;
        try {
            if(isDefaultSet != null){
                if(isDefaultSet.equals("on")) {
                    isDefault = true;
                }
            }
            String address = province + city + area + street;

            Address addressItem = new Address(new Integer(0),accountName,receiverName,address,phoneNumber,postcode,isDefault,province,city,area,street);
            addressDAO.addAddressItem(addressItem);

            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS,"success");
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.toString());
            return Response.status(500).entity(response).build();
        }
    }

    @Path("query")
    @POST
    @Timed
    public Response getUser(@Auth AuthPrincipal principal,
                            @FormParam("login_username") @NotEmpty String userName) {
        if (!Objects.equal(principal.getName(), userName)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }
        try {
            List<Address> addressItem = addressDAO.findByUserName(userName);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, addressItem);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }

    @Path("delete")
    @POST
    @Timed
    public Response deleteAddress(@Auth AuthPrincipal principal,
                                  @QueryParam("login_username") @NotEmpty String userName,
                                  @QueryParam("Ids") @NotEmpty String id) {
        if (!Objects.equal(principal.getName(), userName)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }
        try {
            Long result = addressDAO.deleteAddressById(Integer.parseInt(id));
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, result);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }


}