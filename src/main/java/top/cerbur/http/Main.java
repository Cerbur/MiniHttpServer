package top.cerbur.http;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.run();
    }
}
