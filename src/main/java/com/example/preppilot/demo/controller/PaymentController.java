package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.dto.response.PaymentOrderResponse;
import com.example.preppilot.demo.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // Step 1 — frontend calls this to get order id
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder() throws RazorpayException {
        return ResponseEntity.ok(paymentService.createOrder());
    }

    // Step 2 — Razorpay calls this after payment
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed");
    }
}
