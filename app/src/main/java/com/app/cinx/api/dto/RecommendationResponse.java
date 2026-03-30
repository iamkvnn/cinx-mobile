package com.app.cinx.api.dto;
import java.util.List;
public class RecommendationResponse {
    private String userId;
    private List<RecommendedCourse> recommendations;
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<RecommendedCourse> getRecommendations() {
        return recommendations;
    }
    public void setRecommendations(List<RecommendedCourse> recommendations) {
        this.recommendations = recommendations;
    }
}
