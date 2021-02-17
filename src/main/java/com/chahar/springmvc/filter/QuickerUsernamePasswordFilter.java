package com.chahar.springmvc.filter;

import com.chahar.springmvc.filter.token.OtpUserToken;
import com.chahar.springmvc.otp.OtpGenerator;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QuickerUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {
    private boolean postOnly = true;
    private boolean otpAuthenticationRequired = true;

    private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
    private boolean continueChainBeforeSuccessfulAuthentication = false;
    private OtpGenerator otpGenerator;

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (otpAuthenticationRequired) {
            if (postOnly && !request.getMethod().equals("POST")) {
                throw new AuthenticationServiceException(
                        "Authentication method not supported: " + request.getMethod());
            }

            String username = obtainUsername(request);
            String password = obtainPassword(request);

            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);

            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);

            Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
            logger.info("Quicker: authentication=" + authentication);

            OtpUserToken otpUserToken = new OtpUserToken(authentication, otpGenerator.generateToken());
            SecurityContextHolder.getContext().setAuthentication(otpUserToken);
            request.getSession(false).setAttribute("userToken", otpUserToken);

            return otpUserToken;
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("QuickerUsernamePasswordFilter: Request is to process authentication");

        Authentication authResult;

        try {
            authResult = attemptAuthentication(request, response);
            if (authResult == null) {
                // return immediately as subclass has indicated that it hasn't completed
                // authentication
                return;
            }
            if (authResult instanceof OtpUserToken) {
                String otp = ((OtpUserToken) authResult).getOtp();
                //write otp into response
                String responseContent = "success";

                res.setContentLength(responseContent.length());
                res.getWriter().write(responseContent);
                logger.info(">>> OTP="+ otp+ " for authentication="+ ((OtpUserToken) authResult).getAuthentication());
            } else {
                sessionStrategy.onAuthentication(authResult, request, response);
            }
        } catch (InternalAuthenticationServiceException failed) {
            logger.error("An internal error occurred while trying to authenticate the user.", failed);
            unsuccessfulAuthentication(request, response, failed);

            return;
        } catch (AuthenticationException failed) {
            // Authentication failed
            unsuccessfulAuthentication(request, response, failed);

            return;
        }

        if (authResult instanceof OtpUserToken) {
            //TODO: verify: continue chain
            //chain.doFilter(request, response);
            return;
        } else {
            // Authentication success
            if (continueChainBeforeSuccessfulAuthentication) {
                chain.doFilter(request, response);
            }
            successfulAuthentication(request, response, chain, authResult);
        }

    }

    @Override
    public void setPostOnly(boolean postOnly) {
        super.setPostOnly(postOnly);
        this.postOnly = postOnly;
    }

    public void setOtpAuthenticationRequired(boolean otpAuthenticationRequired) {
        this.otpAuthenticationRequired = otpAuthenticationRequired;
    }

    public void setSessionStrategy(SessionAuthenticationStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public void setContinueChainBeforeSuccessfulAuthentication(
            boolean continueChainBeforeSuccessfulAuthentication) {
        this.continueChainBeforeSuccessfulAuthentication = continueChainBeforeSuccessfulAuthentication;
    }

    public void setOtpGenerator(OtpGenerator otpGenerator) {
        this.otpGenerator = otpGenerator;
    }
}
