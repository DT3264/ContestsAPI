package com.cernadaniel.contestsapi.contests_api.Controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class DefaultErrorController implements ErrorController{
    @RequestMapping("/error")
    @ResponseBody
    public String getErrorPath() {
        return "Out of bounds, you might want to go back to the root page.";
    }

}