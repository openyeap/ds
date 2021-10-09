//package ltd.fdsa.job.controller;
//
//import ltd.fdsa.job.admin.annotation.PermissionLimit;
//import ltd.fdsa.job.jpa.entity.JobGroup;
//import ltd.fdsa.job.jpa.entity.JobUser;
//import ltd.fdsa.job.jpa.service.JobGroupService;
//import ltd.fdsa.job.jpa.service.JobUserService;
//import ltd.fdsa.job.jpa.service.impl.JobUserServiceImpl;
//import ltd.fdsa.job.util.I18nUtil;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.DigestUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/user")
//public class UserController {
//
//    @Resource
//    private JobUserService JobUserDao;
//    @Resource
//    private JobGroupService JobGroupDao;
//
//    @RequestMapping
//    @PermissionLimit(adminuser = true)
//    public String index(Model model) {
//
//        // 执行器列表
//        List<JobGroup> groupList = JobGroupDao.findAll();
//        model.addAttribute("groupList", groupList);
//
//        return "user/user.index";
//    }
//
//    @RequestMapping("/pageList")
//    @ResponseBody
//    @PermissionLimit(adminuser = true)
//    public Map<String, Object> pageList(
//            @RequestParam(required = false, defaultValue = "0") int start,
//            @RequestParam(required = false, defaultValue = "10") int length,
//            String username,
//            int role) {
//
//        // page list
////        List<JobUser> list = JobUserDao.pageList(start, length, username, role);
////        int list_count = JobUserDao.pageListCount(start, length, username, role);
//
//        // package result
//        Map<String, Object> maps = new HashMap<String, Object>();
////        maps.put("recordsTotal", list_count); // 总记录数
////        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
////        maps.put("data", list); // 分页列表
//        return maps;
//    }
//
//    @RequestMapping("/add")
//    @ResponseBody
//    @PermissionLimit(adminuser = true)
//    public Result<String> add(JobUser JobUser) {
//
//        // valid username
//        if (!StringUtils.hasText(JobUser.getUsername())) {
//            return Result.fail(500,
//                    I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
//        }
//        JobUser.setUsername(JobUser.getUsername().trim());
//        if (!(JobUser.getUsername().length() >= 4 && JobUser.getUsername().length() <= 20)) {
//            return Result.fail(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
//        }
//        // valid password
//        if (!StringUtils.hasText(JobUser.getPassword())) {
//            return Result.fail(500,
//                    I18nUtil.getString("system_please_input") + I18nUtil.getString("user_password"));
//        }
//        JobUser.setPassword(JobUser.getPassword().trim());
//        if (!(JobUser.getPassword().length() >= 4 && JobUser.getPassword().length() <= 20)) {
//            return Result.fail(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
//        }
//        // md5 password
//        JobUser.setPassword(DigestUtils.md5DigestAsHex(JobUser.getPassword().getBytes()));
//
//        // check repeat
//        JobUser existUser = JobUserDao.loadByUserName(JobUser.getUsername());
//        if (existUser != null) {
//            return Result.fail(500, I18nUtil.getString("user_username_repeat"));
//        }
//
//        // write
//        JobUserDao.update(JobUser);
//        return Result.success();
//    }
//
//    @RequestMapping("/update")
//    @ResponseBody
//    @PermissionLimit(adminuser = true)
//    public Result<String> update(HttpServletRequest request, JobUser JobUser) {
//
//        // avoid opt login seft
//        JobUser loginUser = (JobUser) request.getAttribute(JobUserServiceImpl.LOGIN_IDENTITY_KEY);
//        if (loginUser.getUsername().equals(JobUser.getUsername())) {
//            return Result.fail(500, I18nUtil.getString("user_update_loginuser_limit"));
//        }
//
//        // valid password
//        if (StringUtils.hasText(JobUser.getPassword())) {
//            JobUser.setPassword(JobUser.getPassword().trim());
//            if (!(JobUser.getPassword().length() >= 4 && JobUser.getPassword().length() <= 20)) {
//                return Result.fail(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
//            }
//            // md5 password
//            JobUser.setPassword(DigestUtils.md5DigestAsHex(JobUser.getPassword().getBytes()));
//        } else {
//            JobUser.setPassword(null);
//        }
//
//        // write
//        JobUserDao.update(JobUser);
//        return Result.success();
//    }
//
//    @RequestMapping("/remove")
//    @ResponseBody
//    @PermissionLimit(adminuser = true)
//    public Result<String> remove(HttpServletRequest request, int id) {
//
//        // avoid opt login seft
//        JobUser loginUser = (JobUser) request.getAttribute(JobUserServiceImpl.LOGIN_IDENTITY_KEY);
//        if (loginUser.getId() == id) {
//            return Result.fail(500, I18nUtil.getString("user_update_loginuser_limit"));
//        }
//
//        JobUserDao.deleteById(id);
//        return Result.success();
//    }
//
//    @RequestMapping("/updatePwd")
//    @ResponseBody
//    public Result<String> updatePwd(HttpServletRequest request, String password) {
//
//        // valid password
//        if (password == null || password.trim().length() == 0) {
//            return Result.fail(500, "密码不可为空");
//        }
//        password = password.trim();
//        if (!(password.length() >= 4 && password.length() <= 20)) {
//            return Result.fail(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
//        }
//
//        // md5 password
//        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
//
//        // update pwd
//        JobUser loginUser = (JobUser) request.getAttribute(JobUserServiceImpl.LOGIN_IDENTITY_KEY);
//
//        // do write
//        JobUser existUser = JobUserDao.loadByUserName(loginUser.getUsername());
//        existUser.setPassword(md5Password);
//        JobUserDao.update(existUser);
//
//        return Result.success();
//    }
//}
