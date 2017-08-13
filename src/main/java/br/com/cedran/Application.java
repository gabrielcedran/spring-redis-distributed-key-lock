package br.com.cedran;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableAspectJAutoProxy
public class Application {

//    @Autowired
//    private RedisTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //@Override
    public void run(String... args) throws Exception {
//        ValueOperations<String, String> ops = this.template.opsForValue();
//        String key = "spring.boot.redis.test";
//        if (!this.template.hasKey(key)) {
//            ops.set(key, "foo");
//        }
//        System.out.println("Found key " + key + ", value=" + ops.get(key));
    }

}
