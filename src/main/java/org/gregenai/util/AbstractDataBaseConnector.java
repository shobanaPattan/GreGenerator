package org.gregenai.util;

import org.gregenai.model.GreRequest;

public abstract class AbstractDataBaseConnector {

    public abstract String readRecords();

    public abstract int createRecords(GreRequest greRequest);

    public abstract void updateRecords(GreRequest greRequest);

    public abstract int deleteRecords(GreRequest greRequest);
}
