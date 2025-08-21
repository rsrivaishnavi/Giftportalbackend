package com.vaishnavi.giftportal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity

public class RevokedToken {
    @Id
    private String token;
    private Date revokedAt;

    public String getToken() {
        return token;
    }

    public Date getRevokedAt() {
        return revokedAt;
    }

    public RevokedToken(String token) {
        this.token = token;
        this.revokedAt = new Date();
    }
}