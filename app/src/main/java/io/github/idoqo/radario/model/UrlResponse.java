package io.github.idoqo.radario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlResponse {
    @JsonProperty("can_create_topic")
    private boolean canCreateTopic;

    public UrlResponse(){
        super();
    }

    public boolean isCanCreateTopic(){
        return canCreateTopic;
    }

    public void setCanCreateTopic(boolean val){
        this.canCreateTopic = val;
    }
}
