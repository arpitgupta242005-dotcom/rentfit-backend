
package com.rentfit.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ConcurrentMap<String, OtpData> otpStore =
            new ConcurrentHashMap<>();

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email) {
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        otpStore.put(email.toLowerCase(), new OtpData(
                otp, System.currentTimeMillis() + 5 * 60 * 1000
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
            otpStore.remove(email.toLowerCase());
            throw new IllegalStateException("Unable to send OTP email", ex);
        }
    }

    public boolean verifyOtp(String email, String otp) {
        String key = email.toLowerCase();
        OtpData data = otpStore.get(key);

        if (data == null || System.currentTimeMillis() > data.expiryTime) {
            otpStore.remove(key);
            return false;
        }

        if (data.otp.equals(otp)) {
            otpStore.remove(key);
            return true;
        }

        return false;
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
