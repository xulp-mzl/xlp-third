package org.xlp.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * 用来判断是否要导出的字段
 * 
 * @author 徐龙平
 *         <p>
 *         2016-11-20
 *         </p>
 * @version 1.0
 * 
 */
// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Retention(RetentionPolicy.RUNTIME)
// 定义注解的作用目标**作用范围字段
@Target({ ElementType.FIELD })
// 说明该注解将被包含在javadoc中
@Documented
public @interface ExcelField {
	public String name() default "";
	public String description() default "";
}
