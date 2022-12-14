package xyz.doikki.videoplayer.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示部分支持的功能
 */
@Retention(RetentionPolicy.SOURCE)
public @interface PartialFunc {
    String message() default "";
}
