package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.Pair;
import util.Util;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginUserController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(LoginUserController.class);

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String method = request.getMethod();
		if ("POST".equals(method)) {
			doPost(request, response);
		}
	}

	@Override
	void doPost(HttpRequest request, HttpResponse response) {
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		
		Collection<String> param = new ArrayList<String>(Arrays.asList(userId, password));
		if (Util.isNullOrEmpty(param)) {
			log.debug("Login fail. Required userId, password");
		} else {
			User user = DataBase.findUserById(userId);
			String cookie = "";
			if (user != null && password.equals(user.getPassword())) {
				cookie = "logined=true";
				log.debug("Login success.");
			} else {
				cookie = "logined=false";
				log.debug("Login failed.");
			}
			response.addHeaderProperty(new Pair("Set-Cookie", cookie));
		}
		response.redirect("index.html");
	}

	@Override
	void doGet(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
	}

}
