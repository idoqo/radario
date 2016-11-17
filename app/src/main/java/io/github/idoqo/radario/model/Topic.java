package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import io.github.idoqo.radario.helpers.Poster;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {
    private String id;
    private String title;
    @JsonProperty("category_id")
    private int category;
    @JsonProperty("like_count")
    private int likeCount;
    @JsonProperty("posts_count")
    private int postsCount;
    @JsonProperty("created_at")
    private String createdAtString;

    private Poster op;

    @JsonProperty("posters")
    private List<Poster> posters;

    //the username of the original poster, typically gotten by querying with the poster's id
    private String posterUsername = null;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }

    public List<Poster> getPosters() {
        return this.posters;
    }

    public Poster getPoster() {
        for (Poster poster : this.posters) {
            if (poster.getDescription().startsWith(Poster.OP_DESCRIPTION)) {
                return poster;
            }
        }
        //should never happen!
        return null;
    }

    public void setPoster(Poster poster) {
        this.op = poster;
    }

    public void setPosterUsername(String name) {
        posterUsername = name;
    }

    public String getPosterUsername(){
        return posterUsername;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public String getCreatedAtString() {
        return createdAtString;
    }

    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
    }

    public Date getCreatedAtAsDate() throws ParseException{
        ISO8601DateFormat df = new ISO8601DateFormat();
        return df.parse(createdAtString);
    }
}
