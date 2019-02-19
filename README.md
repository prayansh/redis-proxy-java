# Redis-Proxy-Demo
A redis proxy implemented as an HTTP web service which allows the ability to add
additional features on top of Redis (e.g. caching).

# Running code
To run the tests 
- `./mvnw test` (will run SpringBoot Tests) (Requires a redis instance to be running at localhost:6379)
- Config variables can be changed in `src/main/resources/application.properties`

To serve app using docker
```
docker-compose up
docker-machine ip default (get ip address)
curl -X GET http://<ip-address>:8000/?key=foo (Retrieve foo from Redis Proxy)
```
- Config variables can be changed in `./docker.properties`

## Architectural Overview
Using Spring boot for serving application as a REST Service. The REST API exposes 3 core functions -> `GET /`, `PUT /`, `GET /ping`. More details about usage below. Uses a LRU Cache implementation to store recently used values with the ability to configure the `cache_capacity` and `timeToExpireInMillis` for the cache. The cache is implemented using a doubly linked list which keeps track of the most recently used and least recently used item, and also supports moving items to the front of the queue if they have been recently accessed.
Cache exposes 6 core functions 
- `retrieve(String key)` - Retrieve key from cache
- `add(String key, String value)` - Add key with given value
- `updateKeyIfPresent(String key)` - Update value of key if present in cache
- `hasKey(String key)` - Check if cache has a key without retrieving
- `size()` - Current size of the cache
- `flush()` - Clear all entries in the cache

## Algorithmic Complexity
ignoring the web request overhead the `retrieve()` function has a best case runtime of `O(1)` and average runtime of `O(k)` where `k` is the read access time from the Redis instance. 

## Time taken 
- 2 hrs on Cache Implementation
- 3 hrs on serving as a REST Service
- 4 hrs on learning to use Docker and integrating app with redis
Learned a lot in the process, so didn't mind the time I took to learn Docker.

# What I would do better
I was a bit limited mostly due to time constraints. 
I started off by creating my own `LRUDoublyLinkedList` implementation which has a private Node class, which was my own wrongdoing. I tried creating this class as its own entity and decoupled it from the `RedisCache` class so much that it will definitely impact performance.
In hindsight my LinkedList implementation is a bit confusing since the delete function is doing two things at a time, i.e. search and delete (due to my design decision as discussed above). I could have made this better by storing the Node rather than the CacheItem in the Map thus allowing me to delete using a node rather than the node content, reducing the process of iterating over the cache to find the appropriate one. This would be the first change to this implementation, if given time.
I would also like to support storage of data structures, rather than just plain Strings.