package com.vaishnavi.giftportal.controller;

import com.vaishnavi.giftportal.entity.GiftProvider;
import com.vaishnavi.giftportal.entity.ProviderProfile;
import com.vaishnavi.giftportal.entity.User;
import com.vaishnavi.giftportal.repository.GiftProviderRepository;
import com.vaishnavi.giftportal.repository.ProviderProfileRepository;
import com.vaishnavi.giftportal.repository.UserRepository;
import com.vaishnavi.giftportal.service.GiftProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/gift-providers")
@CrossOrigin(origins = "https://giftportalfrontend-theta.vercel.app/")
public class GiftProviderController {

    @Autowired
    private GiftProviderService giftProviderService;

    @Autowired
    private GiftProviderRepository giftProviderRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Apply as a gift provider — automatically links the logged-in user.
     */
    @PostMapping("/giftprovider/apply")
    public ResponseEntity<?> applyAsGiftProvider(@RequestBody GiftProvider provider, Principal principal) {
        try {
            // Get the logged-in user's email from JWT/Principal
            String email = principal.getName();

            // Fetch full user from DB
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Link the GiftProvider with this user
            provider.setUser(user);

            // Save to DB
            GiftProvider saved = giftProviderRepository.save(provider);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<GiftProvider> getAllGiftProviders() {
        return giftProviderService.getAllGiftProviders();
    }

   @GetMapping("/{id}")
    public GiftProvider getGiftProviderById(@PathVariable int id) {
        return giftProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find user with id: " + id));
    }


    @PutMapping("/review/{id}")
    public ResponseEntity<String> reviewApplication(@PathVariable int id,
                                                    @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            String comments = payload.get("reviewerComments");

            giftProviderService.updateStatus(id, status, comments);
            return ResponseEntity.ok("Application " + status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process application: " + e.getMessage());
        }
    }

    @PostMapping("/notify/{id}")
    public ResponseEntity<String> notifyApplicant(@PathVariable int id) {
        try {
            giftProviderService.sendManualNotification(id);
            return ResponseEntity.ok("Email notification sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }

    @PutMapping("/providers/{id}/status")
    public ResponseEntity<String> updateProviderStatus(@PathVariable Integer id,
                                                       @RequestBody String status) {
        Optional<GiftProvider> optionalProvider = giftProviderRepository.findById(id);

        if (optionalProvider.isPresent()) {
            GiftProvider provider = optionalProvider.get();
            provider.setStatus(status);
            giftProviderRepository.save(provider);

            if ("APPROVED".equalsIgnoreCase(status)) {
                ProviderProfile profile = new ProviderProfile(
                        provider.getBusinessName(),
                        provider.getEmail(),
                        provider.getPhoneNumber(),
                        provider.getStatus(),
                        provider.getReviewerComments()
                );
                providerProfileRepository.save(profile);
            }

            return ResponseEntity.ok("Status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider not found.");
        }
    }

    @GetMapping("/providers/approved")
    public List<GiftProvider> getApprovedProviders() {
        return giftProviderRepository.findByStatusIgnoreCase("APPROVED");
    }

    @DeleteMapping("/user/{id}")
    public String deleteuser(@PathVariable int id) {
        GiftProvider f = giftProviderRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Delete the user"));
        giftProviderRepository.deleteById(f.getId());
        return "Deleted the user id";
    }

    @GetMapping("/giftprovider/track")
    public ResponseEntity<?> trackApplication(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GiftProvider giftProvider = giftProviderRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return ResponseEntity.ok(giftProvider);
    }

}
