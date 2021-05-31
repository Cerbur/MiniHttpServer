package top.cerbur.http.control;

import top.cerbur.http.annotation.http.Controller;
import top.cerbur.http.annotation.http.GetMapping;

@Controller
public class TestController {
    @GetMapping("/test")
    public String yes() {
        System.out.println("test");
        return "test";
    }
}
