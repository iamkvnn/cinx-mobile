package com.app.cinx.api.dto;

import java.util.List;

public class ReviewReactionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String reviewId;
    public String getReviewId() { return reviewId; }
    public void setReviewId(String val) { this.reviewId = val; }

    private Boolean liked;
    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean val) { this.liked = val; }

}