package util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class HttpRequestUtils {
    /**
     * @param queryString은
     *            URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param 쿠키
     *            값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    /**
     * @param header
     *            값은 	field1: value1
     *            		field2: value2 형식임
     * @return
     */
    
    public static Map<String, String> parseHeaders(String header) {
        return parseValues(header, "\n", ": ");
    }
    
    /**
     * @param header
     *            값은 	field1: value1형식임
     * @return
     */
    
    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }
    
    /**
     * @param 쿠키
     *            값은 GET /index.html HTTP/1.1 형식임
     * @return
     */
    public static Uri parseUri(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            return null;
        }

        String[] tokens = uri.split(" ");
        if (tokens.length != 3) {
            return null;
        }

        return new Uri(tokens[0], tokens[1], tokens[2]);
    }
    
    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }
    
    private static Map<String, String> parseValues(String values, String separator, String valueSeperator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, valueSeperator)).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }
    
}
