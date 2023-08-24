package com.ecom.plantygreen.service;

public interface AuthService {
    String generateToken(User user);
    boolean validateToken(String token);
    User signup(SignupRequestBody signupRequest);
    String login(LoginRequestBody loginRequestBody);
}
