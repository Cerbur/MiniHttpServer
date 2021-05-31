package top.cerbur.http.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.cerbur.http.Server;
import top.cerbur.http.annotation.http.RequestParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class GetServlet implements HttpServlet{
    private static GetServlet instance;
    private GetServlet() {

    }
    public static synchronized GetServlet getInstance() {
        if (instance == null) {
            instance = new GetServlet();
        }
        return instance;
    }

    @Override
    public void doHttp(Request request, Response response) {
        String url = request.getUrl();
        Method method = Server.urlGetMap.get(url);
        if (method == null) {
            request.setCode(404);
        } else {
            Parameter[] parameters = method.getParameters();
            Object[] strings = new String[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                boolean annotationPresent = parameters[i].isAnnotationPresent(RequestParameter.class);
                if (annotationPresent) {
                    RequestParameter annotation = parameters[i].getAnnotation(RequestParameter.class);
                    String val = request.getParams().get(annotation.value());
                    strings[i] = val;
                }
            }
            Class<?> declaringClass = method.getDeclaringClass();
            try {
                Object result;
                if (strings.length == 0) {
                    result = method.invoke(Server.classObjectMap.get(declaringClass.getName()));
                } else {
                    result = method.invoke(Server.classObjectMap.get(declaringClass.getName()),strings);
                }
//                if (method.getReturnType() == String.class) {
//                    response.setBody(new StringBuilder((String) result));
//                } else {
                ObjectMapper objectMapper = new ObjectMapper();
                Object o = objectMapper.convertValue(result, method.getReturnType());
                response.setBody(new StringBuilder(objectMapper.writeValueAsString(o)));
//                }
            } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
