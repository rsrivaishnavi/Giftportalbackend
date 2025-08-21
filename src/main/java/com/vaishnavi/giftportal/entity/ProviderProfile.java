package com.vaishnavi.giftportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String providerName;
    private String email;
    private String contact;
    private String status;
    private String reviewerComments;

    public ProviderProfile() {
    }

    public ProviderProfile(String providerName, String email, String contact, String status, String reviewerComments) {
        this.providerName = providerName;
        this.email = email;
        this.contact = contact;
        this.status = status;
        this.reviewerComments = reviewerComments;
    }

    public Integer getId() {
        return id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }
}
