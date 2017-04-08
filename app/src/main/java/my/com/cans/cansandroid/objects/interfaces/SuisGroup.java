package my.com.cans.cansandroid.objects.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import my.com.cans.cansandroid.objects.enums.FormGroup;

/**
 * Created by Rfeng on 05/02/16.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SuisGroup {
    FormGroup value();
}
