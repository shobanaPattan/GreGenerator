package org.gregenai.model;

public class GreRequest {
    // TODO: Create a builder for this model POJO based on annotation framework like lombok
    private String name;
    private String definition;

    private String databaseType;

    //Getter and Setters
    public String getName() {
        return name;
    }

    public void setName() {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition() {
        this.definition = definition;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType() {
        this.databaseType = databaseType;
    }
}
