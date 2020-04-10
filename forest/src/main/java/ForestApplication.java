import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.vertx.core.Verticle;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface ForestApplication {
    Class<? extends Verticle> [] verticleClasses() default {};

    boolean autoScan() default true;
}
