package com.pegasusgroup.riarewards.model;

public class PromotionMd {
    private String id;
    private String subject;
    private String details;
    private String start_date;
    private String end_date;
    private String points;
    private String fb_image;
    private String fb_url;
    private String views;
    private String fb_shares;
    private String claims;
    private String status;
    private String created_by;
    private String created;
    private String modified;
    private String countries;
    private String internal_claimed;
    private String fb_claimed;

    public String getInternal_claimed() {
        return internal_claimed;
    }

    public void setInternal_claimed(String internal_claimed) {
        this.internal_claimed = internal_claimed;
    }

    public String getFb_claimed() {
        return fb_claimed;
    }

    public void setFb_claimed(String fb_claimed) {
        this.fb_claimed = fb_claimed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getFb_image() {
        return fb_image;
    }

    public void setFb_image(String fb_image) {
        this.fb_image = fb_image;
    }

    public String getFb_url() {
        return fb_url;
    }

    public void setFb_url(String fb_url) {
        this.fb_url = fb_url;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getFb_shares() {
        return fb_shares;
    }

    public void setFb_shares(String fb_shares) {
        this.fb_shares = fb_shares;
    }

    public String getClaims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }
}