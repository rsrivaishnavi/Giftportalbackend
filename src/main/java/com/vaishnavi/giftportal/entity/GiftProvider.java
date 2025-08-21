package com.vaishnavi.giftportal.entity;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "gift_providers")
public class GiftProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonManagedReference
    private User user;

    @NotBlank(message = "Business name is required")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @NotBlank(message = "Contact person is required")
    @Column(name = "contact_person")
    private String contactPerson;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10,20}", message = "Phone number should be between 10 and 20 digits")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "reviewer_comments")
    private String reviewerComments;

    @Lob
    @Column(name = "business_details")
    private String businessDetails;

    
    @Column(name = "portfolio_link", length = 2048)
    private String portfolioLink;

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewerComments() { return reviewerComments; }
    public void setReviewerComments(String reviewerComments) { this.reviewerComments = reviewerComments; }

    public String getBusinessDetails() { return businessDetails; }
    public void setBusinessDetails(String businessDetails) { this.businessDetails = businessDetails; }

    public String getPortfolioLink() { return portfolioLink; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
}
