package org.icememo.api

import org.icememo.Result
import org.icememo.model.UpdatePropertyBody
import org.icememo.utils.ReflectUtils
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Transactional
@RequestMapping("gen")
class GeneralController extends BaseController{
    @RequestMapping(method = RequestMethod.POST, value = "/update_property")
    @ResponseBody
    @Deprecated
    Result updateProperty(@RequestBody UpdatePropertyBody body) {
        String cls = body.cls;

        String className = "org.icememo.entity.$cls";
        Class classObject = Class.forName(className);
        def object = generalDao.get(classObject, body.id);

        Class propertyClass = ReflectUtils.getClass(className, body.name)

        def value;
        if (propertyClass == Double) {
            value = new Double(body.value);
        } else {
            value = body.value
        }
        ReflectUtils.set(object, body.name, value)

        if(cls == "User"){
            ReflectUtils.set(data.user, body.name, value)
        }

        //generalDao.update(object)
        return Result.getOk()
    }
}
