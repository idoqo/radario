package io.github.idoqo.radario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    private int id;
    @JsonProperty("notification_type")
    private int type;
    private boolean read;
    @JsonProperty("created_at")
    private String createdTime;
    @JsonProperty("post_number")
    private int postNumber;
    @JsonProperty("topic_id")
    private int topicID;
    @JsonProperty("slug")
    private String excerpt;
    @JsonProperty("data")
    private NotificationData data;

    public static final int TYPE_MENTIONED = 1;
    public static final int TYPE_REPLIED = 2;
    public static final int TYPE_QUOTED = 3;
    public static final int TYPE_EDITED = 4;
    public static final int TYPE_LIKED = 5;
    public static final int TYPE_PRIVATE_MESSAGE = 6;
    public static final int TYPE_CHAT_INVITE = 7;
    public static final int TYPE_CHAT_INVITE_ACCEPTED = 8;
    public static final int TYPE_POSTED = 9;
    public static final int TYPE_MOVED_POST = 10;
    public static final int TYPE_LINKED = 11;
    public static final int TYPE_GRANTED_BADGE = 12;
    public static final int TYPE_INVITED_TO_TOPIC = 13;
    public static final int TYPE_CUSTOM = 14;
    public static final int TYPE_GROUP_MENTIONED = 15;
    public static final int TYPE_GROUP_MESSAGE_SUMMARY = 16;
    public static final int TYPE_WATCHING_FIRST_POST = 17;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(int postNumber) {
        this.postNumber = postNumber;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public NotificationData getData(){
        return data;
    }

    public void setData(NotificationData notificationData){
        this.data = notificationData;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }
}
