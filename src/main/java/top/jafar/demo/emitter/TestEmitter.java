package top.jafar.demo.emitter;

import top.jafar.demo.Request;
import top.jafar.demo.Response;
import top.jafar.demo.SocketServerAutowired;
import top.jafar.demo.utils.AppleUtils;

/**
 * Created by jafar on 2017/9/6.
 */
public class TestEmitter {

    @SocketServerAutowired
    private AppleUtils appleUtils;

    public void greeting(Request request, int name, Response response) {
        System.out.println(request);
        System.out.println(response);
        appleUtils.eatApple();
    }

}
