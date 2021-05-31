package top.cerbur.http.utils;

import top.cerbur.http.servlet.Request;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RequestUtil {
    public static Request parse(InputStream in) throws IOException {
        Request request = new Request();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(in,
                StandardCharsets.UTF_8));
        parseRequestLine(request,bufferedReader);
        // TODO header 解析
        return request;
    }

    private static void parseRequestLine(Request request, BufferedReader reader) throws IOException {
        String first = reader.readLine();
        String[] group = first.split(" ");
        request.setMethod(group[0]);
        parseUrl(group[1],request);
        request.setVersion(group[2]);
    }

    private static void parseUrl(String s, Request request) throws UnsupportedEncodingException {
        // 分割url
        String[] group = s.split("\\?");
        request.setUrl(URLDecoder.decode(group[0], StandardCharsets.UTF_8));
        //有无查询字符串,需要判断
        if (group.length == 2) {
            String[] segment = group[1].split("&");
            for (String kvString : segment) {
                String[] kv = kvString.split("=");
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = "";
                if (kv.length == 2) {
                    value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                }
                request.getParams().put(key, value);
            }
        }
    }
}
