package com.vaishnavi.giftportal.service;

import com.vaishnavi.giftportal.entity.GiftProvider;
import com.vaishnavi.giftportal.entity.User;
import com.vaishnavi.giftportal.repository.GiftProviderRepository;
import com.vaishnavi.giftportal.repository.UserRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiftProviderService {

    private final GiftProviderRepository giftProviderRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public GiftProviderService(GiftProviderRepository giftProviderRepository, JavaMailSender mailSender) {
        this.giftProviderRepository = giftProviderRepository;
        this.mailSender = mailSender;
        this.userRepository = null;
    }

    public GiftProvider save(GiftProvider giftProvider) {
        return giftProviderRepository.save(giftProvider);
    }



    public List<GiftProvider> getAll() {
        return giftProviderRepository.findAll();
    }

    public Optional<GiftProvider> getByUserId(int id) {
        return giftProviderRepository.findByUserId(id);
    }

    public boolean existsByUserId(int userId) {
        return giftProviderRepository.existsByUserId(userId);
    }

    public void updateStatus(int id, String status, String comments) {
        GiftProvider gp = giftProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        gp.setStatus(status.toUpperCase());
        gp.setReviewerComments(comments);
        giftProviderRepository.save(gp);

        sendReviewEmail(gp); // Send email after updating status

        // Optional: If approved, trigger provider creation
        if ("APPROVED".equalsIgnoreCase(status)) {
            // TODO: Call providerService.createFromGiftProvider(gp);
        }
    }
        public void sendManualNotification(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            String toEmail = user.getEmail(); // ensure `getEmail()` exists
            String subject = "Gift Notification";
            String body = "Hi " + user.getName() + ",\n\nYou have received a gift. Please check your portal.\n\nRegards,\nGift Portal Team";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } else {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }
    }

    private void sendReviewEmail(GiftProvider gp) {
        if (gp.getEmail() == null || gp.getEmail().isEmpty()) {
            System.err.println("No email found for gift provider: " + gp.getId());
            return; // prevent crash
        }

        String subject = "Gift Provider Application Status: " + gp.getStatus();
        String message = "Dear " + gp.getContactPerson() + ",\n\n" +
                "Your gift provider application has been reviewed.\n\n" +
                "Status: " + gp.getStatus() + "\n" +
                "Comments: " + gp.getReviewerComments() + "\n\n" +
                "Thank you,\nGift Portal Team";

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(gp.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

 
    public List<GiftProvider> getAllGiftProviders() {
        return giftProviderRepository.findAll();
    }

    public void saveGiftProvider(GiftProvider giftProvider) {
        giftProviderRepository.save(giftProvider);
    }

}
