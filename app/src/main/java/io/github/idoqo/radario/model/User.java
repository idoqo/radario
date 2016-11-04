package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private int id;
    private String username;
    @JsonProperty("avatar_template")
    private String avatarUrl;

    public User(){
        super();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }

    public String getUsername(){
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getId(){
        return id;
    }
}
