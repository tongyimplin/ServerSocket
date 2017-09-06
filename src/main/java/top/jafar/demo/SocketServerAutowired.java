package top.jafar.demo;

import java.lang.annotation.*;

/**
 * Created by jafar on 2017/9/6.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketServerAutowired {
}
