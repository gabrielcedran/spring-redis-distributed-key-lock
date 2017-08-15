package br.com.cedran.redis.distributed.lock.mechanism;

import br.com.cedran.redis.distributed.lock.annotation.DistributedCacheLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class DistributedCacheLockAspect {

    @Around(value = "@annotation(annotation)")
    public Object execute(ProceedingJoinPoint pjp, final DistributedCacheLock annotation) throws Throwable {
        return pjp.proceed();
    }

}
