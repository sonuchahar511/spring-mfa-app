package com.chahar.springmvc.filter.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class OtpUserToken extends PreAuthenticatedAuthenticationToken {
    private Authentication authentication;
    private String otp;

    public OtpUserToken(Authentication authentication, String otp) {
        super(null, null);
        this.authentication = authentication;
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "OtpUserToken{" +
                "authentication=" + authentication +
                ", otp='" + otp + '\'' +
                '}';
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
