package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.Address;
import net.zyexpress.site.api.IdCard;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.dao.AddressDAO;
import net.zyexpress.site.dao.IdCardDAO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by lumengyu on 2016/5/13.
 */
@Path("/idcard")
@Produces(MediaType.APPLICATION_JSON)
public class IdCardManageResource {

    private final IdCardDAO idCardDAO;

    public IdCardManageResource(IdCardDAO idCardDAO) {
        this.idCardDAO = idCardDAO;
    }

    // TODO: pass json object array instead of many form fields
    @Path("/add")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addNewAddress(@Auth AuthPrincipal principal,@QueryParam("login_name") String accountName,
                                  @FormParam("idcard_name") @NotEmpty String idCardName,
                                  @FormParam("Cardnum") String idCardNumber) {
        Boolean isDefault = false;
        try {

            IdCard idCardItem = new IdCard(new Integer(0),accountName,idCardName,idCardNumber);
            idCardDAO.addIdCardItem(idCardItem);
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
                            @FormParam("query_username") @NotEmpty String userName) {
        if (!Objects.equal(principal.getName(), userName)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }
        try {
            List<IdCard> idCardItem = idCardDAO.findByUserName(userName);
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, idCardItem);
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
            Long result = idCardDAO.deleteIdcardById(Integer.parseInt(id));
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, result);
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }

}