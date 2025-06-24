package org.gregenai.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
//public class HTTPHeaderModel {
//    private String responseType;
//
//    private String dataBaseType;

//    public String getDataBaseType() {
//        return dataBaseType;
//    }
//
//    public void setDataBaseType(String dataBaseType) {
//        this.dataBaseType = dataBaseType;
//    }
//
//
//    public String getResType() {
//        return responseType;
//    }
//
//    public String getResponseType(){
//        return responseType;
//    }
//    public void setResponseType(String responseType) {
//        this.responseType = responseType;
//    }

// TODO : Add 3 supported headers and getters and setters for each

//}
@Getter
@Setter
public class HTTPHeaderModel {
    private String responseType;

    private String dataBaseType;

//    public String getDataBaseType(){
//        return dataBaseType;
//    }
//    public String getResponseType(){
//        return responseType;
//    }

}
