package be.cegeka.batchers.taxcalculator.infrastructure.config;

import static java.lang.System.getProperty;

public enum Environment {

    SINGLEJVM(true), MASTER(true), SLAVE(false);

    private boolean isMaster;

    Environment(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public static Environment getCurrentEnvironment() {
        if(springProfilesActive() == null || springProfilesActive().isEmpty()) {
            return SINGLEJVM;
        }
        if (springProfilesActive().contains("remotePartitioningSlave")) {
            return SLAVE;
        }
        return MASTER;
    }

    private static String springProfilesActive() {
        return getProperty("spring.profiles.active");
    }
}
