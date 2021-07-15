package com.lsm.sso1.controller;

import com.lsm.sso1.dto.LoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AllController {

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String main(HttpServletRequest request) {
        if (checkLogin(request)) {
            return "main";
        }
        return "login";
    }

    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    public ModelAndView doLogin(LoginDTO admin, HttpServletResponse response) {
        if (admin.getUsername().equals("admin") && admin.getPassword().equals("admin")) {
            Cookie cookie = new Cookie("ssocookie", "sso");
            //如果是同父域SSO,需要加上下面这一行
            //cookie.setDomain("x.com");
            cookie.setPath("/");
            response.addCookie(cookie);
            Map<String, Object> param = new HashMap<>();
            List<String> list = new ArrayList<>();
            list.add("http://sso2.b.com:8781/addcookie");
            param.put("cookieurl", list);
            param.put("cookieName", "ssocookie");
            param.put("cookieValue", "sso");
            return new ModelAndView("main", "sendparam", param);
        }
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/addcookie", method = RequestMethod.GET)
    public void addcookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public boolean checkLogin(HttpServletRequest request) {
        boolean ok = false;
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("ssocookie") && cookie.getValue().equals("sso")) {
                    ok = true;
                }
            }
        }
        return ok;
    }
}
