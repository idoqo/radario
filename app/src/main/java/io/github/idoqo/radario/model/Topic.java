package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.github.idoqo.radario.helpers.Poster;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {
    private int id;
    private String title;
    @JsonProperty("category_id")
    private int category;
    @JsonProperty("like_count")
    private int likeCount;
    @JsonProperty("posts_count")
    private int postsCount;

    @JsonProperty("posters")
    private List<Poster> posters;
    private Poster op;

    //the username of the original poster, typically gotten by querying with the poster's id
    private String posterUsername = null;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
