package top.cerbur.http;

import top.cerbur.http.annotation.http.Controller;
import top.cerbur.http.annotation.http.GetMapping;
import top.cerbur.http.annotation.http.RequestParameter;
import top.cerbur.http.servlet.GetServlet;
import top.cerbur.http.servlet.Request;
import top.cerbur.http.servlet.Response;
import top.cerbur.http.utils.ClazzUtil;
import top.cerbur.http.utils.RequestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class Server {
    public static Map<String,Method> urlGetMap = new HashMap<>();
    public static Map<String,Object> classObjectMap = new HashMap<>();
    private int port;

    public Server(int port) {
        this.port = port;
        getControllerClazz();
        showBan();
    }

    public Server() {
        this(8080);
//        urlGetMap.forEach((k,v) -> {
//            Class<?> declaringClass = v.getDeclaringClass();
//            try {
//                v.invoke(classObjectMap.get(declaringClass.getName()));
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        });
    }


    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                600, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(4096), Executors.defaultThreadFactory());
        while (true) {
            Socket socket = serverSocket.accept();
            threadPoolExecutor.execute(new Task(socket));
        }

    }




    public void getControllerClazz() {

        Set<Class<?>> clazz = ClazzUtil.getClasses("top.cerbur.http");
        clazz.removeIf(c -> !c.isAnnotationPresent(Controller.class));
        clazz.forEach(c->{
            Method[] declaredMethods = c.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(GetMapping.class)) {
                    if (!classObjectMap.containsKey(c)) {
                        try {
                            classObjectMap.put(c.getName(),c.getDeclaredConstructor().newInstance());
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                    GetMapping annotation = declaredMethod.getAnnotation(GetMapping.class);
                    String value = annotation.value();
                    urlGetMap.put(value,declaredMethod);
                }
            }
        });
    }

    private void showBan() {
        System.out.println("==============server start==============");
        System.out.println("http://localhost:"+this.port+"/");
        if (urlGetMap.size() != 0) {
            urlGetMap.forEach((k,v) -> {
                GetMapping annotation = v.getAnnotation(GetMapping.class);
                System.out.print("Method-Get: " + annotation.value() + "    ");
                Parameter[] parameters = v.getParameters();
                if (parameters.length != 0) {
                    System.out.print("Params: ");
                }
                for (Parameter parameter : parameters) {
                    if (parameter.isAnnotationPresent(RequestParameter.class)) {
                        RequestParameter annotation1 = parameter.getAnnotation(RequestParameter.class);
                        System.out.print(parameter.getType().getName() + ":" + annotation1.value() + ",");
                    }
                }
                System.out.println();
            });
        }
    }




    private class Task implements Runnable {

        private Socket socket;
        public Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("request:start");
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()
            ){
                // 请求解析
                Request request = RequestUtil.parse(inputStream);
                Response response = new Response();
                switch (request.getMethod()) {
                    case "GET":
                        GetServlet.getInstance().doHttp(request,response);
                        break;
                    default:
                        request.setCode(400);
                        break;
                }

                // 响应解析
                response.writeAndFlush(outputStream,request);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("request:end");
        }
    }
}
