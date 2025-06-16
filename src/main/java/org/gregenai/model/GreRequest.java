package org.gregenai.model;

public class GreRequest {
    // TODO: Create a builder for this model POJO based on annotation framework like lombok
    private String name;
    private String definition;
    private String databaseType;

    private GreRequest greRequest;

    public GreRequest(GreBuilder greBuilder) {
        this.name = greBuilder.name;
        this.definition = greBuilder.definition;
    }

    //Getter and Setters
    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public GreRequest getGreRequest() {
        return greRequest;
    }

    public void setDefinition() {
        this.definition = definition;
    }

    public void setName() {
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType() {
        this.databaseType = databaseType;
    }

    public static class GreBuilder {
        private String name;
        private String definition;
        private GreRequest greRequest;

        public GreBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public GreBuilder setDefinition(String definition) {
            this.definition = definition;
            return this;
        }

        public GreBuilder setGreRequestObject(GreRequest greRequest) {
            this.greRequest = greRequest;
            return this;
        }

        public GreRequest build() {
            return new GreRequest(this);
        }

    }
}
