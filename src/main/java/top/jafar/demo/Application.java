package top.jafar.demo;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import top.jafar.demo.utils.AppleUtils;
import top.jafar.demo.utils.OrangeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by jafar on 2017/9/6.
 */
public class Application {

    public static void main(String []args) {
        new Application().invoke("test", "greeting", 11);

    }
    Map<String, Object> injectionPools = new HashMap<>();
    List<Object> injectionList = new ArrayList<>();

    public Application() {

        //准备可以注入的工具类
        AppleUtils appleUtils = new AppleUtils();
        OrangeUtils orangeUtils = new OrangeUtils();

        injectionList.add(appleUtils);
        injectionList.add(orangeUtils);
        injectionList.add(new Request());
        injectionList.add(new Response());


//        System.out.println();
        //循环放入池内
        for (Object injectionObj: injectionList) {
            injectionPools.put(injectionObj.getClass().getName(), injectionObj);
        }

        //扫码emitter目录下的类
    }

    private Object getObject(String name) {
        Object obj = null;
        try {
            Class<?> objClazz = ClassUtils.getClass("top.jafar.demo.emitter." + StringUtils.capitalize(name) + "Emitter");
            obj = objClazz.newInstance();
            //放入之前注入Autowired的属性
            Field[] fieldsWithAnnotation = FieldUtils.getFieldsWithAnnotation(objClazz, SocketServerAutowired.class);
            for (Field wiredField:fieldsWithAnnotation) {
                Object wiredFieldVal = getBean(wiredField.getType().getName());
                FieldUtils.writeField(wiredField, obj, wiredFieldVal, true);
            }
            //将对象放入
            injectionPools.put(objClazz.getName(), obj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private Method getMethod(Class<?> clazz, String name, int argLength) {
        Method method = null;
        if(clazz == null || StringUtils.isBlank(name)) return null;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m:methods) {
            String methodName = m.getName();
            int parameterCount = m.getParameterCount();
            Class<?>[] parameterTypes = m.getParameterTypes();
            for (Class<?> parameterType: parameterTypes) {
                if(isInjectiveType(parameterType)) {
                    parameterCount--;
                }
            }


            if(methodName.equals(name) && parameterCount == argLength) {
                method = m;
                break;
            }
        }
        return method;
    }

    private Object getBean(String name) {
        return injectionPools.get(name);
    }

    private boolean isInjectiveType(Class<?> clazz) {
        for (Object injectObj:injectionList) {
            //如果该类包含了injection包含的类
            if(injectObj.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public Object invoke(String name, String method, Object ...args) {
        //获取对象
        Object targetObj = getObject(name);
        Method invokeMethod = getMethod(targetObj.getClass(), method, args.length);
        try {
            //准备参数
            Class<?>[] parameterTypes = invokeMethod.getParameterTypes();
            Object invokeArgs[] = new Object[parameterTypes.length];
            int argIndex = 0;
            for (int i=0; i< parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if(isInjectiveType(parameterType)) {
                    invokeArgs[i] = getBean(parameterType.getName());
                    continue;
                }
                invokeArgs[i] = args[argIndex];
                argIndex++;
            }

            Object invokeResult = MethodUtils.invokeMethod(targetObj, method, invokeArgs, invokeMethod.getParameterTypes());
            System.out.println(invokeMethod);
            return invokeResult;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
