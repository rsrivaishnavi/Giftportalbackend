package com.vaishnavi.giftportal.dto;

import com.vaishnavi.giftportal.entity.GiftProvider;

public class GiftProviderDTO {

    private int id;
    private String businessName;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String status;
    private String reviewerComments;

    public GiftProviderDTO() {
    }

    public GiftProviderDTO(GiftProvider provider) {
    this.id = provider.getId();
    this.businessName = provider.getBusinessName();
    this.contactPerson = provider.getContactPerson();
    this.email = provider.getEmail();
    this.phoneNumber = provider.getPhoneNumber();
    this.status = provider.getStatus();
    this.reviewerComments = provider.getReviewerComments();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
