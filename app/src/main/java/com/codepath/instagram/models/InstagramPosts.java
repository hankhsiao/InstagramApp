package com.codepath.instagram.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hankxiao on 11/3/15.
 */
public class InstagramPosts implements Serializable {
    public List<InstagramPost> posts;

    public InstagramPosts(List<InstagramPost> posts) {
        this.posts = posts;
    }
}
