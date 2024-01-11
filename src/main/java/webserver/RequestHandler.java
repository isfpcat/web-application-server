package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.Pair;
import util.Uri;
import util.Util;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private Socket connection;
    
    private enum LOGIN {
    	INIT, SUCCESS, FAIL, LOGINED
    }
    private LOGIN loginState = LOGIN.INIT;
    
    
    public RequestHandler (Socket connectionSocket) {
        this.connection = connectionSocket;
    }
    
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	InputStreamReader isr = new InputStreamReader(in);
        	BufferedReader reader = new BufferedReader(isr);
        	
        	HttpRequest request = new HttpRequest(reader);
        	HttpResponse response = new HttpResponse(out);
        	
    		Map<String, String> cookieProperties = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
			String logined = cookieProperties.get("logined");
			if (!Strings.isNullOrEmpty(logined) && Boolean.parseBoolean(logined)) {
    			loginState = LOGIN.LOGINED;
    		}

			String path = request.getPath();
        	String method = request.getMethod();
	        if (!Strings.isNullOrEmpty(path) && !Strings.isNullOrEmpty(method)) {

        		if ("/user/create".equals(path) && "POST".equals(method)) {
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
    			} else if("/user/login".equals(path) && "POST".equals(method)) {
        			String userId = request.getParameter("userId");
    				String password = request.getParameter("password");
					
					Collection<String> param = new ArrayList<String>(Arrays.asList(userId, password));
					if (Util.isNullOrEmpty(param)) {
    					log.debug("Login fail. Required userId, password");
    				} else {
    					User user = DataBase.findUserById(userId);
						String cookie = "";
						if (user != null && password.equals(user.getPassword())) {
							loginState = LOGIN.SUCCESS;
							cookie = "logined=true";
							log.debug("Login success.");
						} else {
							loginState = LOGIN.FAIL;
							cookie = "logined=false";
							log.debug("Login failed.");
						}
						response.addHeaderProperty(new Pair("Set-Cookie", cookie));
    				}
					response.redirect("index.html");
    			} else if("/user/list".equals(path) && "GET".equals(method)) {
    				if (loginState == LOGIN.LOGINED) {
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
    			} else {
    				path = "/".equals(path) ? "/index.html" : path;
    				response.forward(path);
    			}
        	} else {
        		path = "404.html";
        		response.forward(path);
        	}
        	
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
