package org.icememo.api

import org.icememo.Result
import org.icememo.Time
import org.icememo.entity.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@RequestMapping("user")
class UserController extends BaseController{
    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getAll() {
        return userDao.all

    }

    @RequestMapping(value = "select", method = RequestMethod.GET)
    @ResponseBody
    public Result select(Integer id) {
        data.user = userDao.get(id)
        data.user.lastLoginDate = Time.ms
        userDao.update(data.user)

        return Result.getOk()
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    @ResponseBody
    public User create(String name) {
        User user = new User(login: name, lastLoginDate: Time.ms)
        userDao.save(user)

        return user
    }
}
