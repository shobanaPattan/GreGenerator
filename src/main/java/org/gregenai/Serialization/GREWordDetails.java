package org.gregenai.Serialization;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class GREWordDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    String greWord;
    String greDefinition;
    String greExample;

    public GREWordDetails(String greWord, String greDefinition, String greExample) {
        this.greWord = greWord;
        this.greDefinition = greDefinition;
        this.greExample = greExample;
    }

    public String toString() {
        return "GREWordDetails {GreWord :'" + greWord + "',GreDefinition : " + greDefinition + ",GreExample : " + greExample + "}";
    }

}
