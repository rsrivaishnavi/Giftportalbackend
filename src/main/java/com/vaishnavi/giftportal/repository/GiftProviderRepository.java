package com.vaishnavi.giftportal.repository;

import com.vaishnavi.giftportal.entity.GiftProvider;
import com.vaishnavi.giftportal.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftProviderRepository extends JpaRepository<GiftProvider, Integer> {
    boolean existsByUserId(int userId);
    Optional<GiftProvider> findByUserId(int userId);
    List<GiftProvider> findByStatusIgnoreCase(String status);
    Optional<GiftProvider> findByUser(User user);

}
