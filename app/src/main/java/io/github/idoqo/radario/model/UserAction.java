package io.github.idoqo.radario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAction {
    @JsonProperty("action_type")
    private int actionType;
    private boolean deleted;
    private String excerpt;
    @JsonProperty("created_at")
    private String timePosted;
    @JsonProperty("post_id")
    private int parentTopicId;
    @JsonProperty("post_number")
    private int postNumber;
    @JsonProperty("category_id")
    private int categoryId;
    @JsonProperty("reply_to_post_number")
    private int parentPostNumber;
    @JsonProperty("title")
    private String parentTopic;

    public UserAction(){
        super();
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public int getParentTopicId() {
        return parentTopicId;
    }

    public void setParentTopicId(int parentTopicId) {
        this.parentTopicId = parentTopicId;
    }

    public int getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(int postNumber) {
        this.postNumber = postNumber;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getParentPostNumber() {
        return parentPostNumber;
    }

    public void setParentPostNumber(int parentPostNumber) {
        this.parentPostNumber = parentPostNumber;
    }

    public String getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(String parentTopic) {
        this.parentTopic = parentTopic;
    }
}
