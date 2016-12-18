package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private int id;
    private String username;
    @JsonProperty("avatar_template")
    private String avatarUrlTemplate;
    @JsonProperty("name")
    private String fullName;

    public User(){
        super();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setAvatarUrlTemplate(String url) {
        this.avatarUrlTemplate = url;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public String getFullName(){
        return this.fullName;
    }

    public String getAvatarUrl(int size){
        String template = getAvatarUrlTemplate();
        String radarHost = "https://radar.techcabal.com";
        return radarHost+template.replace("{size}", String.valueOf(size));
    }

    public String getUsername(){
        return username;
    }

    public String getAvatarUrlTemplate() {
        return avatarUrlTemplate;
    }

    public int getId(){
        return id;
    }
}
