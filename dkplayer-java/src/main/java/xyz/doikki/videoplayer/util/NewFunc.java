package xyz.doikki.videoplayer.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 新增加的功能
 */
@Retention(RetentionPolicy.SOURCE)
public @interface NewFunc {
    String message() default "";
}
