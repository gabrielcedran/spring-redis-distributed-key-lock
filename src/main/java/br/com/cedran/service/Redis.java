package br.com.cedran.service;

import br.com.cedran.mechanism.CacheManagerCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Redis {

    @Autowired
    private CacheManagerCustom<List, List> cacheManager;

    public void addToList(int number, String key) {
        cacheManager.updateWithLock(lst -> {
            lst.add(number);
            return lst;
        }, () -> {
            List<Integer> lst = new ArrayList();
            lst.add(number);
            return lst;
        }, "counter", "counter");
    }

    public void addToList2(int number, String key) {
        cacheManager.updateWithLock(lst -> {
            lst.add(number);
            return lst;
        }, () -> {
            List<Integer> lst = new ArrayList();
            lst.add(number);
            return lst;
        }, "counter", "counter", 10, 55000);
    }

    @Cacheable(cacheNames = "counter")
    public List<Integer> obtainList(String key) {
        return new ArrayList<>();
    }
}
