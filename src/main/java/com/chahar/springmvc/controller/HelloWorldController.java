
package com.chahar.springmvc.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HelloWorldController {

	@RequestMapping("/helloworld")
	public ModelAndView hello(ModelMap model,Principal principal) {

		String loggedInUserName=principal.getName();
		
		return new ModelAndView("hello", "userName", loggedInUserName);
	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		String errorMsg= (String) request.getParameter("error");
		if(errorMsg!=null) {
			response.setHeader("error", errorMsg);
		}
		return "login";
	}
}

