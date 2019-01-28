package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.main.ClientRestHandler;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.connection.RestResult;
import de.raphaelmuesseler.financer.shared.connection.RestResultStatus;
import de.raphaelmuesseler.financer.shared.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Path("api")
public class FinancerRestService {

    private ExecutorService executor;

    public FinancerRestService(ExecutorService executor) {
        this.executor = executor;
    }

    @POST
    @Path("/checkCredentials")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCredentials(@FormParam("email") String email, @FormParam("password") String password) throws Exception {
        Map<String, Object> newParameters = new HashMap<>();
        newParameters.put("email", email);
        newParameters.put("password", password);

        Future<ConnectionResult> result = this.executor.submit(new ClientRestHandler("checkCredentials", newParameters));
        return Response.ok().entity(connectionResultToRestResult(result.get())).build();
    }

    @POST
    @Path("/registerUser")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@FormParam("email") String email, @FormParam("password") String password,
                                 @FormParam("salt") String salt, @FormParam("name") String name,
                                 @FormParam("surname") String surname, @FormParam("birthDate") String birthdate) throws Exception {
        Map<String, Object> newParameters = new HashMap<>();
        User user = new User(email, password, salt, name, surname, LocalDate.parse(birthdate));
        newParameters.put("user", user);

        Future<ConnectionResult> result = this.executor.submit(new ClientRestHandler("registerUser", newParameters));
        return Response.ok().entity(connectionResultToRestResult(result.get())).build();
    }

    @POST
    @Path("/getUsersCategories")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersCategories(@FormParam("user_id") int id, @FormParam("token") String token) throws Exception {
        Map<String, Object> newParameters = new HashMap<>();
        User user = new User();
        user.setId(id);
        newParameters.put("user", user);
        newParameters.put("token", token);

        Future<ConnectionResult> result = this.executor.submit(new ClientRestHandler("getUsersCategories", newParameters));
        return Response.ok().entity(connectionResultToRestResult(result.get())).build();
    }

    @POST
    @Path("/getTransactions")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@FormParam("user_id") int id, @FormParam("token") String token) throws Exception {
        Map<String, Object> newParameters = new HashMap<>();
        User user = new User();
        user.setId(id);
        newParameters.put("user", user);
        newParameters.put("token", token);

        Future<ConnectionResult> result = this.executor.submit(new ClientRestHandler("getTransactions", newParameters));
        return Response.ok().entity(connectionResultToRestResult(result.get())).build();
    }

    private RestResult connectionResultToRestResult(ConnectionResult connectionResult) {
        RestResult result = new RestResult();
        if (connectionResult.getException() == null) {
            result.setStatus(RestResultStatus.SUCCESS);
            result.setData(connectionResult.getResult());
        } else {
            result.setStatus(RestResultStatus.ERROR);
            if (connectionResult.getException() != null) {
                result.setStatusMessage(connectionResult.getException().getMessage());
            }
        }
        return result;
    }

}
