package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginUserController;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private Socket connection;
    private Map<String, Controller> controllerMap;
    
    public RequestHandler (Socket connectionSocket) {
        this.connection = connectionSocket;
        this.controllerMap = new HashMap<String, Controller>();
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginUserController());
        controllerMap.put("/user/list", new ListUserController());
    }
    
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	InputStreamReader isr = new InputStreamReader(in);
        	BufferedReader reader = new BufferedReader(isr);
        	
        	HttpRequest request = new HttpRequest(reader);
        	HttpResponse response = new HttpResponse(out);

			String path = request.getPath();
			
	        if (!Strings.isNullOrEmpty(path)) {
        		if ("/user/create".equals(path)) {
        			Controller controller = controllerMap.get(path);
        			controller.service(request, response);
    			} else if("/user/login".equals(path)) {
    				Controller controller = controllerMap.get(path);
        			controller.service(request, response);
    			} else if("/user/list".equals(path)) {
    				Controller controller = controllerMap.get(path);
        			controller.service(request, response);
    			} else {
    				response.forward("/".equals(path) ? "/index.html" : path);
    			}
        	} else {
        		response.forward("404.html");
        	}
        	
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
