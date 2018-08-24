package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.util.Map;

public class FinancerService {

    private static FinancerService INSTANCE = null;

    private FinancerService() {}

    public static FinancerService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FinancerService();
        }
        return INSTANCE;
    }

    /**
     * Checks, if the users credentials are correct
     * @param parameters [email, password]
     * @return true, if credentials are correct
     */
    public ConnectionResult<Boolean> checkCredentials(Map<String, Object> parameters) {
        boolean result = false;
        return new ConnectionResult<>(result);
    }

}
