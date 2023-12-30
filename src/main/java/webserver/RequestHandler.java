package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    private Pattern p = Pattern.compile("([\\w-]+): (.+)");
    private Matcher m;
    
    private HashMap<String, String> hm;
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        hm = new HashMap<String, String>();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	String line = "";
        	
        	InputStreamReader isr = new InputStreamReader(in);
        	//바이트 기반의 스트림을 문자 기반의 스트림으로 변환하는 스트림
        	BufferedReader reader = new BufferedReader(isr);
        	//문자 기반의 보조 스트림
        	
        	while(!"".equals((line = reader.readLine()))){
        		//문자 기반의 보조 스트림은 한 줄씩 읽어올 수 있는 메소드를 제공
        		log.debug("line = " + line);
        		Matcher m = p.matcher(line);
        		if (m.matches() && m.groupCount() == 2) {
        			hm.put(m.group(1), m.group(2));
        		} else {
        			String[] result = line.split(" ");
        			if(result.length == 3) {
        				hm.put("Method", result[0]);
        				hm.put("Uri", result[1]);
        				hm.put("Protocol", result[2]);
        			}
        		}
        	}
        	 
        	DataOutputStream dos = new DataOutputStream(out);
        	//자바의 8가지 기본 자료형의 단위로(boolean, byte, char, short, int, long, float, double)
        	//읽고 쓸 수 있는 바이트 기반의 보조 스트림
        	
        	byte[] body = null;
        	if(hm.containsKey("Uri")) {
        		body = Files.readAllBytes(new File("./webapp" + hm.get("Uri")).toPath());
        	} else {
        		body = "Hello World".getBytes();
        	}
    		response200Header(dos, body.length);
            responseBody(dos, body);
        	
        	isr.close();
        	reader.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
