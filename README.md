# spring-redis-distributed-key-lock

Simple solution to provide distributed lock by key when you need to guarantee that at least two concurrent processing do not override each other's changes.
It provides configurable number of retries and delay between attempts out-of-the-box.

### Usage:
To provide distributed lock when updating a key, you just have to inject the CacheManagerCustom and call the method updateWithLock, passing the following parameters:

1. Function: Operation to be performed when there is an existent element for that key in cache
2. Supplier: The value to be applied to the cache when that key doesn't exist yet
3. String: Cache Name
4. Object: Cache Key
5. Integer: Number Of Attempts
6. Integer: Delay Between Attempts

If the number of retries exceeds, a LockException will be thrown.

Warning: You should not perform lock in your entire application because it could cause performance issues.
It should be used only where you really need to guarantee that concurrent processes don't overwrite each other.

#### Example of usage:


```java
    @Autowired
    private CacheManagerCustom<List, List> cacheManager;

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
```

#### To be done:
1. Create method guarantees lock but doesn't perform retries at all.
2. Allow process to not perform anything when there is no record in cache