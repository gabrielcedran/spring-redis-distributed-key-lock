package br.com.cedran.app.Service;

import br.com.cedran.redis.distributed.lock.mechanism.CacheManagerCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Redis {

    public static final String CACHE_NAME = "counter";
    @Autowired
    private CacheManagerCustom<List, List> cacheManager;

    public void addToListNotConfigurable(int number, String key) {
        cacheManager.updateWithLock(lst -> {
            lst.add(number);
            return lst;
        }, () -> {
            List<Integer> lst = new ArrayList();
            lst.add(number);
            return lst;
        }, CACHE_NAME, key);
    }

    public void addToListConfigurable(int number, String key) {
        cacheManager.updateWithLock(lst -> {
            lst.add(number);
            return lst;
        }, () -> {
            List<Integer> lst = new ArrayList();
            lst.add(number);
            return lst;
        }, CACHE_NAME, key, 5, 100);
    }

    @Cacheable(cacheNames = "counter")
    public List<Integer> obtainList(String key) {
        return new ArrayList<>();
    }
}
