package org.icememo.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("tools")
public class ToolsController extends BaseController {

    /*--------Translate database--------
    1)Copy XML to dispatcher-servlet.xml
    2)Remove @GeneratedValue for all beans
    3)Go to http://localhost:8080/tools/translate-bd

    <!--bean id="dataSource2" class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
        <property name="driverClass" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:file:${ICE_MEMO_MAIN_FOLDER}/main2;DB_CLOSE_DELAY=-1;MV_STORE=FALSE;MVCC=FALSE"/>
        <property name="username" value="sa"/>
        <property name="password" value=""/>
    </bean>

    <bean id="sessionFactory2"
          class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource2"/>
        <property name="packagesToScan" value="org.icememo"/>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.show_sql">true</prop>
                <prop key="format_sql">true</prop>
                <prop key="use_sql_comments">true</prop>
                <prop key="show_sql">true</prop>

                <prop key="hibernate.connection.charSet">UTF-8</prop>
                <prop key="hibernate.enable_lazy_load_no_trans">true</prop>
                <prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>

                <prop key="hibernate.connection.characterEncoding">UTF-8</prop>
                <prop key="hibernate.connection.useUnicode">true</prop>
                <prop key="hibernate.hbm2ddl.auto">${hibernate.hbm2ddl.auto}</prop>
            </props>
        </property>
    </bean-->

    @Autowired
    @Qualifier("sessionFactory2")
    LocalSessionFactoryBean sessionFactory2

    @RequestMapping("translate-bd")
    @ResponseBody
    public Result translateBd(Integer id) {
        if (!debugMode()) {
            return
        }

        Session session = sessionFactory2.object.openSession()

        Package entityPackage = User.class.package
        for (Class aClass : ReflectUtils.getClasses(entityPackage.name)) {
            //println(aClass)
            List<Object> objects = generalDao.getAll(aClass)
            //println(objects.size())

            Transaction tx = session.beginTransaction();
            for(Object object: objects){
                session.save(object)
            }

            tx.commit();
        }
        session.close()

        return Result.getOk()
    }*/
}
