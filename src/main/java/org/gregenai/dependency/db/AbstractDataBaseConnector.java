package org.gregenai.dependency.db;

import org.gregenai.model.GreRequest;

public abstract class AbstractDataBaseConnector {

    public abstract String readRecords();

    public abstract String readRecordsByName(GreRequest greRequest);

    public abstract String createRecords(GreRequest greRequest);

    public abstract String updateRecords(GreRequest greRequest);

    public abstract String deleteRecords(GreRequest greRequest);

    public abstract String readViewsCount(GreRequest greRequest);

    public abstract String readNameByViewsCount(GreRequest greRequest);
}
