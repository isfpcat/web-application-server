package controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.Util;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController{
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String method = request.getMethod();
		if ("POST".equals(method)) {
			doPost(request, response);
		}
	}

	@Override
	void doPost(HttpRequest request, HttpResponse response) {
		try {
			String userId = request.getParameter("userId");
			String password = request.getParameter("password");
			String name = request.getParameter("name");
			String email = URLDecoder.decode(request.getParameter("email"), "utf-8");
			
			Collection<String> param = new ArrayList<String>(Arrays.asList(userId, password, name, email));
			if (Util.isNullOrEmpty(param)) {
				log.debug("Create user fail. Required userId, password, name, email");
			} else {
				User user = new User(userId, password, name, email);
				DataBase.addUser(user);
				log.debug("Create user information = " + user.toString());
			}
			
			response.redirect("index.html");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	void doGet(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		
	}

}
