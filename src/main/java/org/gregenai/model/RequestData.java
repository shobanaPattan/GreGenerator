package org.gregenai.model;

public class RequestData {
    private String databaseType;
    private GreRequest greRequest;

    public RequestData(String databaseType, GreRequest greRequest) {
        this.databaseType = databaseType;
        this.greRequest = greRequest;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public GreRequest getGreRequest() {
        return greRequest;
    }
}
