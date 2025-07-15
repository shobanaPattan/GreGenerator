package org.gregenai.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// TODO : Add 3 supported headers and getters and setters for each
@Getter
@Setter
@Data
@Builder
public class HTTPHeaderModel {
    private String responseType;
    @Getter
    private String dataBaseType;

//    private String userNameType;

}
