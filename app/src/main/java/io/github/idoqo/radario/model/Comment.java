package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private int id;
    private String username;
    @JsonProperty("avatar_template")
    private String avatarUrl;
    @JsonProperty("post_number")
    private int postNumber;
    private int postType;
    private double score;
    //is the comment a reply to another comment?
    private boolean commentIsReply;
    @JsonProperty("reply_to_post_number")
    private Integer parentCommentId;
    //comment text
    private String cooked;

    public void setCommentIsReply(boolean isReply){
        commentIsReply = isReply;
    }

    public void setCommentIsReply(){
        commentIsReply = (parentCommentId != null);
    }

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(int postNumber) {
        this.postNumber = postNumber;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isCommentReply() {
        return commentIsReply;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getCooked() {
        return cooked;
    }

    public void setCooked(String cooked) {
        this.cooked = cooked;
    }
}
