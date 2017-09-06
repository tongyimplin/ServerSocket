import org.apache.commons.lang3.CharSetUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by jafar on 2017/9/6.
 */
public class TestHello {

    @Test
    public void testGreeting() {
        System.out.println(new Hello().greeting());;
    }

    //计算字符串中包含某字符数.
    @Test
    public void testCalc() {

        System.out.println(CharSetUtils.delete("The quick brown fox jumps over the lazy dog.", "ae"));
    }

    @Test
    public void testMan() {

    }

}
