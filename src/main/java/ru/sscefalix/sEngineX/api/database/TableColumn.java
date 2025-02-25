package ru.sscefalix.sEngineX.api.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableColumn {
    String name();

    boolean primaryKey() default false;

    boolean autoIncrement() default false;

    boolean nullable() default true;

    boolean index() default false;

    String defaultValue() default "";
}
