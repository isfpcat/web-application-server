package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

import util.HttpRequestUtils;
import util.IOUtils;
import util.Uri;

public class HttpRequest {
	private BufferedReader reader;
	
	private String method;
	private String path;
	
	private Map<String, String> headerMaps;
	private Map<String, String> userParams;
	
	public HttpRequest (BufferedReader in) {
		reader = in;
		
		String line = "";
    	try {
    		line = reader.readLine();
    		if (line == null) {
    			return;
    		}
    		Uri uri = HttpRequestUtils.parseUri(line);
			method = uri.getMethod();
			path = uri.getPath();
    		
			String header = "";
			while(!"".equals((line = reader.readLine()))){
				if (line == null) break;
				header += line+"\n";
			}
			headerMaps = HttpRequestUtils.parseHeaders(header);
			
			if ("POST".equals(method)) {
				String length = headerMaps.get("Content-Length");
				if(!Strings.isNullOrEmpty(length)) {
					String body = IOUtils.readData(reader, Integer.parseInt(length));
					userParams = HttpRequestUtils.parseQueryString(body);
				}
			} if ("GET".equals(method) && 
					uri.getPath().split("\\?").length == 2) {
				String[] uriTokens = uri.getPath().split("\\?");
				path = uriTokens[0];
				userParams = HttpRequestUtils.parseQueryString(uriTokens[1]);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public String getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getHeader(String field) {
		return headerMaps.get(field);
	}
	
	public String getParameter(String field) {
		return userParams.get(field);
	}
}
