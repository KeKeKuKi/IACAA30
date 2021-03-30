package com.gapache.security.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * @since 2020/8/6 4:02 下午
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthResource {

    /**
     * 资源作用域
     *
     * @return 资源作用域
     */
    String scope();

    /**
     * 资源名称
     *
     * @return 资源名称
     */
    String name();
}
