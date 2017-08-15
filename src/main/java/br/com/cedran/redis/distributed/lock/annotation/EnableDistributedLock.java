package br.com.cedran.redis.distributed.lock.annotation;

import br.com.cedran.redis.distributed.lock.Application;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(Application.class)
public @interface EnableDistributedLock {
}
