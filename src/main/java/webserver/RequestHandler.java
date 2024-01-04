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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.HttpRequestUtils.Uri;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private HashMap<String, String> requestHeaderMap;
    private Socket connection;
    
    private enum LOGIN {
    	INIT, SUCCESS, FAIL, LOGINED
    }
    private int loginState = LOGIN.INIT.ordinal();
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.requestHeaderMap = new HashMap<String, String>();
    }
    
    private String[] parseUserParams(BufferedReader reader) {
    	String requestBody = "";
		if(requestHeaderMap.containsKey("Content-Length")) {
			try {
				requestBody = util.IOUtils.readData(reader, Integer.parseInt(requestHeaderMap.get("Content-Length")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return requestBody.split("&");
    }

    private void mappingHttpHeaderTohashMap(BufferedReader reader) {
    	String line = "";
    	try {
    		line = reader.readLine();
    		if(line == null) {
    			return;
    		}
    		Uri uri = HttpRequestUtils.parseUri(line);
			requestHeaderMap.put("Method", uri.getMethod());
			requestHeaderMap.put("Uri", uri.getUri());
			requestHeaderMap.put("Protocol", uri.getProtocol());
    		
			while(!"".equals((line = reader.readLine()))){
				Pair pair = HttpRequestUtils.parseHeader(line);
				if (pair != null) {
					requestHeaderMap.put(pair.getKey(), pair.getValue());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        log.debug("requestHeaderMap {} ", requestHeaderMap.size());

        // 클라이언트가 전송하는 데이터는 InputStream을 통해 읽을 수 있다. 
        // 서버는 outputStream을 통해 데이터를 전달할 수 있다.
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	InputStreamReader isr = new InputStreamReader(in);
        	//바이트 기반의 스트림을 문자 기반의 스트림으로 변환하는 스트림
        	BufferedReader reader = new BufferedReader(isr);
        	//문자 기반의 보조 스트림
        	
        	mappingHttpHeaderTohashMap(reader);
        	if (requestHeaderMap.containsKey("Cookie")) {
        		Map<String, String> cookieProperties = util.HttpRequestUtils.parseCookies(requestHeaderMap.get("Cookie"));
        		
        		if (cookieProperties.containsKey("logined")) {
        			String loginProperty = cookieProperties.get("logined");
        			if (Boolean.parseBoolean(loginProperty)) {
            			loginState = LOGIN.LOGINED.ordinal();
            		}
        		}
        	}
        	
        	DataOutputStream dos = new DataOutputStream(out);
        	//자바의 8가지 기본 자료형의 단위로(boolean, byte, char, short, int, long, float, double)
        	//읽고 쓸 수 있는 바이트 기반의 보조 스트림
        	byte[] body = {};
        	
	        if (requestHeaderMap.containsKey("Uri") && requestHeaderMap.containsKey("Method")) {
	        	String uri = requestHeaderMap.get("Uri");
	        	String method = requestHeaderMap.get("Method");
	        	
	        	if (("/".equals(uri) || "/index.html".equals(uri)) && "GET".equals(method)) {
	        		body = Files.readAllBytes(new File("./webapp/index.html").toPath());
	        		
	        		response200Header(dos, body.length);
            		responseBody(dos, body);
	        	} else if ("/user/create".equals(uri) && "POST".equals(method)) {
	        			String[] userParams = parseUserParams(reader);
	        			String userId = "";
	    				String password = "";
	    				String name = "";
	    				String email = "";
	        				
	    				for(String param : userParams) {
	    					String[] property = param.split("=");
							if(property.length == 2) {
								String key = property[0];
								String value = property[1];
								
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
						}
	    				
						User user = new User(userId, password, name, email);
						DataBase.addUser(user);
						log.debug("Create user information = " + user.toString());
						
						response302Header(dos);
	            		responseBody(dos, body);
	    			} else if("/user/login".equals(uri) && "POST".equals(method)) {
	    				String[] userParams = parseUserParams(reader);
	        			String userId = "";
	    				String password = "";
	    				
						for(String param : userParams) {
							String[] property = param.split("=");
							if(property.length == 2) {
								String key = property[0];
								String value = property[1];
								
								if ("userId".equals(key)) {
									userId = value;
								} else if("password".equals(key)) {
									password = value;
								} 
							}
						}
						
						User user = DataBase.findUserById(userId);
						if (user != null && password.equals(user.getPassword())) {
							loginState = LOGIN.SUCCESS.ordinal();
							log.debug("Login success.");
	    				  	response302HeaderWithCookie(dos, "logined=true");
		            		responseBody(dos, body);
						} else {
							loginState = LOGIN.FAIL.ordinal();
							log.debug("Login failed.");
							response302HeaderWithCookie(dos, "logined=false");
		            		responseBody(dos, body);
						}
						
	    			} else if("/user/list".equals(uri) && "GET".equals(method)) {
	    				if (loginState == LOGIN.LOGINED.ordinal()) {
	    					StringBuilder userListHtml = new StringBuilder();
	    					Collection<User> usrList = DataBase.findAll();
	    					for (User user : usrList) {
	    						String name = user.getName();
	    						String email = user.getEmail();
	    						userListHtml.append(name + " " + email + "\n");
	    					}
	    					body = userListHtml.toString().getBytes();
	    				  	response200Header(dos, body.length);
		            		responseBody(dos, body);
	    				} else {
	    				  	log.debug("Redirect to login.html ");
	    				  	response302Header(dos);
		            		responseBody(dos, body);
	    				}
	    			} else {
	    				body = Files.readAllBytes(new File("./webapp" + uri).toPath());
	            		response200Header(dos, body.length);
	            		responseBody(dos, body);
	    			}
        	} else {
        		body = Files.readAllBytes(new File("./webapp/404.html").toPath());
        		response200Header(dos, body.length);
        		responseBody(dos, body);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
    	 try {
			 dos.writeBytes("HTTP/1.1 200 OK \r\n");
			 String type = "text/html";
             if (requestHeaderMap.containsKey("Uri")) {
            	 String uri = requestHeaderMap.get("Uri");
            	 if (uri.contains("css")) type = "text/css";
             }
             dos.writeBytes("Content-Type: " + type + "; charset=utf-8\r\n");
             dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");	 
             dos.writeBytes("\r\n");	
         } catch (IOException e) {
             log.error(e.getMessage());
         }
    }
    
    private void response302Header(DataOutputStream dos) {
   	    try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String cookie) {
   	    try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
			dos.writeBytes("Set-Cookie: " + cookie +"; path=/ \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
