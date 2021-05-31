package top.cerbur.http.control;

import top.cerbur.http.annotation.http.Controller;
import top.cerbur.http.annotation.http.GetMapping;
import top.cerbur.http.annotation.http.RequestParameter;
import top.cerbur.http.entity.User;

@Controller
public class MyController {

    @GetMapping("/hello")
    public String hello(@RequestParameter("param") String param) {
        return param;
    }

    @GetMapping("/yes")
    public User yes() {
        User user = new User();
        user.setName("Cerbur");
        user.setAge(20);
        return user;
    }
}
