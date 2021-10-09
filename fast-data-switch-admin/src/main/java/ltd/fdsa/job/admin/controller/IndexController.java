package ltd.fdsa.job.admin.controller;

import lombok.var;
import ltd.fdsa.job.admin.jpa.service.SystemUserService;
import ltd.fdsa.switcher.core.job.model.Result;
import ltd.fdsa.switcher.core.util.CookieUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * index controller
 */
@Controller
public class IndexController extends BaseController {


    @Resource
    private SystemUserService loginService;

    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {

//        Map<String, Object> dashboardMap = JobService.dashboardInfo();
//        model.addAllAttributes(dashboardMap);

        return "index";
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    public Result<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
//        Result<Map<String, Object>> chartInfo = JobService.chartInfo(startDate, endDate);
//        return chartInfo;
        return null;
    }

    @RequestMapping("/login")
//    @PermissionLimit(limit = false)
    public String toLogin() {
        if (loginService.checkLogin(request, response) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
//    @PermissionLimit(limit = false)
    public Result<String> doLogin(String username, String password, String remember) {
        var result = loginService.login(username, password);
        if (result.getCode() == 200 || result.getCode() == 0) {

            CookieUtil.set(response, SystemUserService.USER_LOGIN_IDENTITY, result.getData(), "on".equals(remember));
        }
        return result;
    }

    @RequestMapping(value = "logout", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
//    @PermissionLimit(limit = false)
    public Result<String> logout() {
        CookieUtil.remove(request, response, SystemUserService.USER_LOGIN_IDENTITY);
        return Result.success();
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

}
