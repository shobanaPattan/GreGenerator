package org.gregenai.util;

import org.gregenai.model.GreRequest;

public abstract class AbstractDataBaseConnector {

    public abstract String readRecords();

    public abstract String readRecordsByName(GreRequest greRequest);

    public abstract String createRecords(GreRequest greRequest);

    public abstract void updateRecords(GreRequest greRequest);

    public abstract String deleteRecords(GreRequest greRequest);
}
