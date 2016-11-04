package io.github.idoqo.radario.helpers;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * model class for topic posters, the posters come with different descriptions such as Original Poster, etc
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Poster {

    private String description;
    @JsonProperty("user_id")
    private int userId;

    //string used to mark the poster as returned from the Discourse API
    public static final String OP_DESCRIPTION = "Original Poster";

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
