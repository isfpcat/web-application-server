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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.Pair;
import util.Uri;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private HashMap<String, String> requestHeaderMap;
    private Socket connection;
    private int statusCode;
    
    private enum LOGIN {
    	INIT, SUCCESS, FAIL, LOGINED
    }
    private LOGIN loginState = LOGIN.INIT;
    private Collection<Pair> responseHeaderProperty;
    
    public RequestHandler (Socket connectionSocket) {
        this.connection = connectionSocket;
        this.requestHeaderMap = new HashMap<String, String>();
        this.responseHeaderProperty = new ArrayList<Pair>();
    }
    
    private Map<String, String> parseUserParams(BufferedReader reader) {
    	String requestBody = "";
		if (requestHeaderMap.containsKey("Content-Length")) {
			try {
				requestBody = util.IOUtils.readData(reader, Integer.parseInt(requestHeaderMap.get("Content-Length")));
				log.debug("requestBody = " + requestBody);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return HttpRequestUtils.parseQueryString(requestBody);
    }

    private void mappingHttpHeaderTohashMap(BufferedReader reader) {
    	String line = "";
    	try {
    		line = reader.readLine();
    		if (line == null) {
    			return;
    		}
    		Uri uri = HttpRequestUtils.parseUri(line);
			requestHeaderMap.put("Method", uri.getMethod());
			requestHeaderMap.put("Uri", uri.getUri());
			requestHeaderMap.put("Protocol", uri.getProtocol());
    		
			String header = "";
			while(!"".equals((line = reader.readLine()))){
				header += line+"\n";
			}
			
			Map<String, String> headerMap = HttpRequestUtils.parseHeaders(header);
			for (String key : headerMap.keySet()) {
				requestHeaderMap.put(key, headerMap.get(key));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	InputStreamReader isr = new InputStreamReader(in);
        	BufferedReader reader = new BufferedReader(isr);
        	
        	mappingHttpHeaderTohashMap(reader);
        	
        	if (requestHeaderMap.containsKey("Cookie")) {
        		Map<String, String> cookieProperties = HttpRequestUtils.parseCookies(requestHeaderMap.get("Cookie"));
        		
        		if (cookieProperties.containsKey("logined")) {
        			String loginProperty = cookieProperties.get("logined");
        			if (Boolean.parseBoolean(loginProperty)) {
            			loginState = LOGIN.LOGINED;
            		}
        		}
        	}
        	
        	DataOutputStream dos = new DataOutputStream(out);
        	byte[] body = {};
        	statusCode = 200;
        	
	        if (requestHeaderMap.containsKey("Uri") && requestHeaderMap.containsKey("Method")) {
		        	String uri = requestHeaderMap.get("Uri");
		        	String method = requestHeaderMap.get("Method");

	        		if ("/user/create".equals(uri) && "POST".equals(method)) {
	        			Map<String, String> userParams = parseUserParams(reader);
	        			String userId = "";
	    				String password = "";
	    				String name = "";
	    				String email = "";
	    				
	    				for(String key : userParams.keySet()) {
							String value = userParams.get(key);
							
							if("userId".equals(key)) {
								userId = value;
							} else if("password".equals(key)) {
								password = value;
							} else if("name".equals(key)) {
								name = value;
							} else if("email".equals(key)) {
								email = URLDecoder.decode(value, "utf-8");
							}
						}
	    				
						User user = new User(userId, password, name, email);
						DataBase.addUser(user);
						statusCode = 302;
						
						log.debug("Create user information = " + user.toString());
	    			} else if("/user/login".equals(uri) && "POST".equals(method)) {
	    				Map<String, String> userParams = parseUserParams(reader);
	        			String userId = "";
	    				String password = "";
	    				
						for(String key : userParams.keySet()) {
							String value = userParams.get(key);
							if ("userId".equals(key)) {
								userId = value;
							} else if("password".equals(key)) {
								password = value;
							} 
						}
						
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
						
						statusCode = 302;
						responseHeaderProperty.add(new Pair("Set-Cookie", cookie));
						
	    			} else if("/user/list".equals(uri) && "GET".equals(method)) {
	    				if (loginState == LOGIN.LOGINED) {
	    					StringBuilder userListHtml = new StringBuilder();
	    					Collection<User> usrList = DataBase.findAll();
	    					for (User user : usrList) {
	    						String name = user.getName();
	    						String email = user.getEmail();
	    						userListHtml.append(name + " " + email + "\n");
	    					}
	    					
	    					body = userListHtml.toString().getBytes();
	    				} else {
	    					statusCode = 302;
	    				  	log.debug("Redirect to login.html ");
	    				}
	    			} else {
	    				body = Files.readAllBytes(new File("./webapp" + ("/".equals(uri) ? "/index.html" : uri)).toPath());
	    			}
        	} else {
        		body = Files.readAllBytes(new File("./webapp/404.html").toPath());
        	}
	        
	        if (requestHeaderMap.containsKey("Accept")) {
	        	String accept = requestHeaderMap.get("Accept").split(",")[0];
	        	responseHeaderProperty.add(new Pair("Content-Type", accept));
	        }
	        
	        if (body.length > 0) {
	        	responseHeaderProperty.add(new Pair("Content-Length", String.valueOf(body.length)));
	        }
	        
	        ResponseHandler.response(dos, statusCode, responseHeaderProperty, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
