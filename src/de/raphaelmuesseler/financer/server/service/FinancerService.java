package de.raphaelmuesseler.financer.server.service;

public class FinancerService {

    private static FinancerService INSTANCE = null;

    private FinancerService() {}

    public static FinancerService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FinancerService();
        }
        return INSTANCE;
    }

}
