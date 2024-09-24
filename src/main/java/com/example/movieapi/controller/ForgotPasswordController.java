package com.example.movieapi.controller;


import com.example.movieapi.auth.User;
import com.example.movieapi.dto.MailBody;
import com.example.movieapi.entities.ForgotPassword;
import com.example.movieapi.repositories.ForgotPasswordRepository;
import com.example.movieapi.repositories.UserRepository;
import com.example.movieapi.services.EmailService;
import com.example.movieapi.utils.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Please provide a valid email")) ;

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request:" + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMail(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> varifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("Please provide an valid email"));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user).orElseThrow(() -> new RuntimeException("Invalid OTP for email" + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword,
                                                 @PathVariable String email){
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String encoderPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encoderPassword);

        return ResponseEntity.ok("Password has been changed");
    }


    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
