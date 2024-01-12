package controller;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class ListUserController extends AbstractController{
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String method = request.getMethod();
		if ("GET".equals(method)) {
			doGet(request, response);
		}
	}

	@Override
	void doPost(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
	}

	@Override
	void doGet(HttpRequest request, HttpResponse response) {
		Map<String, String> cookieProperties = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
		String logined = cookieProperties.get("logined");
		
		if (!Strings.isNullOrEmpty(logined) && Boolean.parseBoolean(logined)) {
			StringBuilder userListHtml = new StringBuilder();
			Collection<User> usrList = DataBase.findAll();
			for (User user : usrList) {
				String name = user.getName();
				String email = user.getEmail();
				userListHtml.append(name + " " + email + "\n");
			}
			response.forwardWithBody(userListHtml.toString().getBytes());
		} else {
			response.redirect("index.html");
		  	log.debug("Redirect to login.html ");
		}
	}
}
