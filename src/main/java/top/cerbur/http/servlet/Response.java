package top.cerbur.http.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    String version="HTTP/1.1";
    String status = "200 OK";
    Map<String, String> headers = new HashMap<>();
    StringBuilder body = new StringBuilder();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public StringBuilder getBody() {
        return body;
    }

    public void setBody(StringBuilder body) {
        this.body = body;
    }

    public Response() {
        headers.put("Content-Type", "text/html; charset=utf-8");
    }

    public void writeAndFlush(OutputStream outputStream,Request request) throws IOException {
        this.version = request.getVersion();
        switch (request.getCode()) {
            case 404:
                this.status = "404 Not Found";
                break;
            case 500:
                this.status = "500 BadRequest";
                break;
            default:break;
        }
        String response = buildResponse();
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    //组装响应
    private String buildResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        // 拼接响应行
        responseBuilder.append(version).append(" ");
        responseBuilder.append(status);
        responseBuilder.append("\r\n");

        // 拼接响应头
        int contentLength = body.toString().getBytes(StandardCharsets.UTF_8).length;
        setHeader("Content-Length", String.valueOf(contentLength));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey());
            responseBuilder.append(": ");
            responseBuilder.append(entry.getValue());
            responseBuilder.append("\r\n");
        }

        // 空行
        responseBuilder.append("\r\n");

        // 响应正文
        responseBuilder.append(body);

        return responseBuilder.toString();
    }

    private void setHeader(String s, String valueOf) {
        headers.put(s,valueOf);
    }
}
