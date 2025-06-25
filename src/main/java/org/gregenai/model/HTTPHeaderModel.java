package org.gregenai.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// TODO : Add 3 supported headers and getters and setters for each
@Data
@Builder
public class HTTPHeaderModel {
    private String responseType;
    private String dataBaseType;
}
