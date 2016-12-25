package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentUser {
    private int id;
    private String username;
    @JsonProperty("avatar_template")
    private String avatarUrlTemplate;
    @JsonProperty("name")
    private String fullName;
    @JsonProperty("total_unread_notifications")
    private int totalUnreadNotifications;
    @JsonProperty("unread_notifications")
    private int unreadNotifications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrlTemplate() {
        return avatarUrlTemplate;
    }

    public void setAvatarUrlTemplate(String avatarUrlTemplate) {
        this.avatarUrlTemplate = avatarUrlTemplate;
    }

    public String getAvatarUrl(int size){
        String template = getAvatarUrlTemplate();
        String radarHost = "https://radar.techcabal.com";
        return radarHost+template.replace("{size}", String.valueOf(size));
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTotalUnreadNotifications() {
        return totalUnreadNotifications;
    }

    public void setTotalUnreadNotifications(int totalUnreadNotifications) {
        this.totalUnreadNotifications = totalUnreadNotifications;
    }

    public int getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(int unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }
}
