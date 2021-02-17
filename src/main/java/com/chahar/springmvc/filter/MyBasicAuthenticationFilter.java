package com.chahar.springmvc.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyBasicAuthenticationFilter extends org.springframework.security.web.authentication.www.BasicAuthenticationFilter{
	private static final Log LOGGER = LogFactory.getLog(MyBasicAuthenticationFilter.class);
	
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
		
	public MyBasicAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
		super(authenticationManager, authenticationEntryPoint);
	}
	
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,Authentication authResult) throws IOException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("MyBasicAuthenticationFilter:>>>>>> User is authenticated: "+authResult.getDetails());
		}
	}
	
	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exp) throws IOException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("MyBasicAuthenticationFilter:>>>>>> Authentication failed Reason="+exp.getCause());
		}
	}
	
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Basic ")) {
			try {
				String[] failed = this.extractAndDecodeHeader(header, request);

				assert failed.length == 2;

				String username = failed[0];
				boolean isAuthenticationRequired =  this.authenticationIsRequired(username);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("MyBasicAuthenticationFilter:>>>>>> Basic Authentication Authorization header found for user \'" + username + "\'");
					LOGGER.debug("isAuthenticationRequired="+isAuthenticationRequired);
				}
				
				if (isAuthenticationRequired) {
					UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
							failed[1]);
					authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
					Authentication authResult = getAuthenticationManager().authenticate(authRequest);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("MyBasicAuthenticationFilter:>>>>>> Authentication success: " + authResult);
					}

					SecurityContextHolder.getContext().setAuthentication(authResult);
					this.rememberMeServices.loginSuccess(request, response, authResult);
					this.onSuccessfulAuthentication(request, response, authResult);
				}
			} catch (AuthenticationException arg9) {
				SecurityContextHolder.clearContext();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("MyBasicAuthenticationFilter:>>>>>> Authentication request for failed: " + arg9);
				}

				this.rememberMeServices.loginFail(request, response);
				this.onUnsuccessfulAuthentication(request, response, arg9);
				if (this.isIgnoreFailure()) {
					chain.doFilter(request, response);
				} else {
					this.getAuthenticationEntryPoint().commence(request, response, arg9);
				}

				return;
			}

			chain.doFilter(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}
	
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {
		byte[] base64Token = header.substring(6).getBytes("UTF-8");

		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException arg6) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, this.getCredentialsCharset(request));
		int delim = token.indexOf(":");
		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		} else {
			return new String[] { token.substring(0, delim), token.substring(delim + 1) };
		}
	}

	private boolean authenticationIsRequired(String username) {
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		return existingAuth != null && existingAuth.isAuthenticated()
				? (existingAuth instanceof UsernamePasswordAuthenticationToken
						&& !existingAuth.getName().equals(username) ? true
								: existingAuth instanceof AnonymousAuthenticationToken)
				: true;
	}
	
	public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
		super.setAuthenticationDetailsSource(authenticationDetailsSource);
	}
	
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
		super.setRememberMeServices(rememberMeServices);
	}

	public void setCredentialsCharset(String credentialsCharset) {
		super.setCredentialsCharset(credentialsCharset);
	}

}
