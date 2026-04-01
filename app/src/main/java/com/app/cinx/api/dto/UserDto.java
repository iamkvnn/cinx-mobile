package com.app.cinx.api.dto;

public class UserDto {
    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String name;
    public String getName() { return name; }
    public void setName(String val) { this.name = val; }

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String role;
    public String getRole() { return role; }
    public void setRole(String val) { this.role = val; }

    private String gender;
    public String getGender() { return gender; }
    public void setGender(String val) { this.gender = val; }

    private String avatarUrl;
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String val) { this.avatarUrl = val; }

    private Integer xp;
    public Integer getXp() { return xp; }
    public void setXp(Integer val) { this.xp = val; }

    private String createdAt;
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String val) { this.createdAt = val; }

    private String joinedAt;
    public String getJoinedAt() { return joinedAt; }
    public void setJoinedAt(String val) { this.joinedAt = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

    private Boolean instructorVerified;
    public Boolean getInstructorVerified() { return instructorVerified; }
    public void setInstructorVerified(Boolean val) { this.instructorVerified = val; }

    private Boolean verified;
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean val) { this.verified = val; }

    private Boolean isVerified;
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean val) { this.isVerified = val; }

    private Boolean active;
    public Boolean getActive() { return active; }
    public void setActive(Boolean val) { this.active = val; }

    private Boolean isActive;
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean val) { this.isActive = val; }

    private Boolean locked;
    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean val) { this.locked = val; }

    private Boolean isLocked;
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean val) { this.isLocked = val; }

    public boolean isInstructorApproved() {
        if (instructorVerified != null) return instructorVerified;
        if (verified != null) return verified;
        if (isVerified != null) return isVerified;
        return status != null && ("VERIFIED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status));
    }

    public boolean isLockedAccount() {
        if (locked != null) return locked;
        if (isLocked != null) return isLocked;
        return status != null && "LOCKED".equalsIgnoreCase(status);
    }

    public boolean isActiveAccount() {
        if (active != null) return active;
        if (isActive != null) return isActive;
        return !isLockedAccount();
    }

    public String getJoinDateDisplay() {
        if (createdAt != null && !createdAt.isEmpty()) return createdAt;
        if (joinedAt != null && !joinedAt.isEmpty()) return joinedAt;
        return "N/A";
    }

}