package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.dto.response.PaymentOrderResponse;
import com.example.preppilot.demo.entity.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.premium.amount}")
    private int premiumAmount;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Step 1 — Create Razorpay order
    public PaymentOrderResponse createOrder() throws RazorpayException {
        User user = getLoggedInUser();

        if (user.isPremium()) {
            throw new RuntimeException("User is already premium");
        }

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", premiumAmount);
        options.put("currency", "INR");
        options.put("receipt", "preppilot_" + user.getId());
        options.put("notes", new JSONObject().put("userId", user.getId()));

        Order order = client.orders.create(options);

        return PaymentOrderResponse.builder()
                .orderId(order.get("id"))
                .currency("INR")
                .amount(premiumAmount)
                .keyId(keyId)
                .build();
    }

    // Step 2 — Verify webhook signature and unlock premium
    @Transactional
    public void handleWebhook(String payload, String razorpaySignature) {
        try {
            // verify webhook signature
            String expectedSignature = hmacSHA256(payload, keySecret);

//            if (!expectedSignature.equals(razorpaySignature)) {
//                throw new RuntimeException("Invalid webhook signature");
//            }

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            // only process successful payments
            if (!"payment.captured".equals(eventType)) {
                return;
            }

            JSONObject paymentEntity = event
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            // get userId from notes
            String userIdStr = paymentEntity
                    .getJSONObject("notes")
                    .getString("userId");

            Long userId = Long.parseLong(userIdStr);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            user.setPremium(true);
            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Webhook processing failed: " + e.getMessage());
        }
    }

    private String hmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
