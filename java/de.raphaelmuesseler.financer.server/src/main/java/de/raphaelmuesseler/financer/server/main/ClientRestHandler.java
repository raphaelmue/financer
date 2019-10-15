package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRestHandler implements Callable<ConnectionResult> {

    private Logger logger;
    private FinancerService service = FinancerService.getInstance();
    private String methodName;
    private Map<String, Serializable> parameters;

    public ClientRestHandler(String methodName, Map<String, Serializable> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.logger = Logger.getLogger("ClientRestHandler");
    }

    public ConnectionResult<Serializable> call() {
        this.logger.log(Level.INFO, "New client request.");

        ConnectionResult<Serializable> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (!methodName.equals("checkCredentials") && !methodName.equals("registerUser")) {
                User user = FinancerService.getInstance().checkUsersToken(this.logger, session, parameters);
                if (user == null || user.getId() != ((User) parameters.get("user")).getId()) {
                    throw new NotAuthorizedException("Token '" + parameters.get("token") + "' is invalid.");
                }
            }

            Method method;
            try {
                method = FinancerService.class.getMethod(methodName, Logger.class, Map.class);
                //noinspection unchecked
                result = (ConnectionResult<Serializable>) method.invoke(this.service, this.logger, session, parameters);
                this.logger.log(Level.INFO, "Request has been successfully handled.");
            } catch (Exception exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
                result = new ConnectionResult<>(null, exception);
            }
        } catch (NotAuthorizedException exception) {
            result = new ConnectionResult<>(null, exception);
            this.logger.log(Level.SEVERE, exception.getMessage());
        }
        return result;
    }

}
