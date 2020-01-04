package org.justd.simple.provider.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhangjd
 * @title: SimpleProvider
 * @description:
 * @date 2020/1/320:06
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SimpleProvider {
    Class<?> value();
}
