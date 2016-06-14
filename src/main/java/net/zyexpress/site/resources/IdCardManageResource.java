package net.zyexpress.site.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import io.dropwizard.auth.Auth;
import net.zyexpress.site.api.IdCard;
import net.zyexpress.site.api.RestfulResponse;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.dao.IdCardDAO;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by lumengyu on 2016/5/13.
 */
@Path("/idcard")
@Produces(MediaType.APPLICATION_JSON)
public class IdCardManageResource {

    private final IdCardDAO idCardDAO;
    private final String uploadDir;

    public IdCardManageResource(IdCardDAO idCardDAO, String uploadDir) {
        this.idCardDAO = idCardDAO;
        this.uploadDir = uploadDir;
    }

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


    @Path("/query")
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

    @Path("/delete")
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


    // TODO: what does idCardName in idCardItem table mean?

    @Path("/picture")
    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addPicture(@Auth AuthPrincipal principal,
                               @FormDataParam("userName") String userName,
                               @FormDataParam("idCardId") String idCardId,
                               @FormDataParam("idCardFront") InputStream idCardFront,
                               @FormDataParam("idCardFront") FormDataContentDisposition contentDispositionFront,
                               @FormDataParam("idCardBack") InputStream idCardBack,
                               @FormDataParam("idCardBack") FormDataContentDisposition contentDispositionBack)
            throws IOException {
        if (!Objects.equal(principal.getName(), userName)) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Not authorized - have you logged in?");
            return Response.status(403).entity(response).build();
        }

        IdCard cardItem = idCardDAO.findByCardId(idCardId);
        if (cardItem != null) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                    "Id Number already exist in database.");
            return Response.status(400).entity(response).build();
        }

        try {
            java.nio.file.Path dirPath = Paths.get(uploadDir, idCardId);
            java.nio.file.Path frontPath = Paths.get(uploadDir, idCardId, "front_" + contentDispositionFront.getFileName());
            java.nio.file.Path backPath = Paths.get(uploadDir, idCardId, "back_" + contentDispositionBack.getFileName());

           /* if (Files.exists(dirPath)) {
                RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED,
                        "Bad Request, picture already exists.");
                return Response.status(400).entity(response).build();
            }*/
            Files.createDirectories(dirPath);
            Files.copy(idCardFront, frontPath);
            Files.copy(idCardBack, backPath);

            IdCard idCard = new IdCard(-1, userName, userName, idCardId, false);
            idCardDAO.addIdCardItem(idCard);

            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.SUCCESS, "success");
            return Response.status(200).entity(response).build();
        } catch (Exception ex) {
            RestfulResponse response = new RestfulResponse(RestfulResponse.ResponseStatus.FAILED, ex.getMessage());
            return Response.status(500).entity(response).build();
        }
    }
}