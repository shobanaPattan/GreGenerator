package org.gregenai.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class GreRequest {
    private String name;
    private String definition;
    private String databaseType;
}
