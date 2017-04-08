package my.com.cans.cansandroid.objects;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Rfeng on 04/09/16.
 */
public class BaseModel {
    public Boolean isValid() {
        Field[] fieldList = this.getClass().getFields();
        for (Field field : fieldList) {
            Annotation nonNull = field.getAnnotation(NonNull.class);
            if (nonNull != null) {
                try {
                    Object value = field.get(this);
                    if (value == null || value.equals(""))
                        return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
