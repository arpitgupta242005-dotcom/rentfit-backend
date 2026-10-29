
package com.rentfit.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ConcurrentMap<String, OtpData> otpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Long> verifiedEmails =
            new ConcurrentHashMap<>();

    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000;
    private static final long VERIFIED_EXPIRY_MS = 10 * 60 * 1000;

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email) {
        String emailKey = normalizeEmail(email);

        // A new OTP requires fresh verification.
        verifiedEmails.remove(emailKey);

        String otp = String.format(
                "%06d", secureRandom.nextInt(1_000_000)
        );

        otpStore.put(emailKey, new OtpData(
                otp, System.currentTimeMillis() + OTP_EXPIRY_MS
        ));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Rentfit Email Verification");
        message.setText(
                "Your Rentfit verification OTP is: " + otp
                        + "\n\nThis code expires in 5 minutes."
        );

        try {
            mailSender.send(message);
        } catch (RuntimeException ex) {
            otpStore.remove(emailKey);
            throw new IllegalStateException(
                    "Unable to send OTP email", ex
            );
        }
    }

    public boolean verifyOtp(String email, String otp) {
        String emailKey = normalizeEmail(email);
        OtpData data = otpStore.get(emailKey);

        if (data == null
                || System.currentTimeMillis() > data.expiryTime) {
            otpStore.remove(emailKey);
            return false;
        }

        if (!data.otp.equals(otp)) {
            return false;
        }

        otpStore.remove(emailKey);

        verifiedEmails.put(
                emailKey,
                System.currentTimeMillis() + VERIFIED_EXPIRY_MS
        );

        return true;
    }

    public boolean consumeVerifiedEmail(String email) {
        String emailKey = normalizeEmail(email);
        Long expiryTime = verifiedEmails.remove(emailKey);

        return expiryTime != null
                && System.currentTimeMillis() <= expiryTime;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static class OtpData {
        private final String otp;
        private final long expiryTime;

        private OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
