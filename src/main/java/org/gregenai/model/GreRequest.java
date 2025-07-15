package org.gregenai.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Data
@Builder
public class GreRequest {
    private String name;
    private String definition;
    private String databaseType;


    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
}
