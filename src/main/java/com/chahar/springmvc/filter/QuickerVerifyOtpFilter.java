package com.chahar.springmvc.filter;

import com.chahar.springmvc.filter.token.OtpUserToken;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QuickerVerifyOtpFilter extends AbstractAuthenticationProcessingFilter {
    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public QuickerVerifyOtpFilter() {
        super(new AntPathRequestMatcher("/verifyOtp", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final String otpInput = request.getParameter("inputOtp");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof OtpUserToken){
            OtpUserToken otpUserToken = (OtpUserToken) authentication;
            if(otpUserToken.getOtp().equals(otpInput)){
                //successful authentication
                Authentication actualAuthentication = otpUserToken.getAuthentication();

                /*successHandler.onAuthenticationSuccess(request, response, authentication);*/

                return actualAuthentication;
            }else{
                throw new InternalAuthenticationServiceException("OTP mismatched");
            }
        }

        return null;
    }

}
