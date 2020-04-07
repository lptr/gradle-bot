import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class Launcher {
    public static void launch(Class<?> appClass) {
    }

    public static Future<Injector> launch(List<Class<? extends Module>> moduleClasses, List<Class<? extends Verticle>> verticleClasses) {
        Vertx vertx = Vertx.vertx();
        List<Module> modules = moduleClasses.stream().map(klass -> {
            try {
                return klass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Vertx.class).toInstance(vertx);
            }
        });
        Injector injector = Guice.createInjector(modules);
        return Future.succeededFuture(injector);
    }
}
