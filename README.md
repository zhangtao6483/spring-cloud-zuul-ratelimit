# spring-cloud-zuul-ratelimit

Spring cloud zuul 限流 Filter


- 支持对请求来源，请求URL的不同维度限流
- 支持计数器法，令牌桶算法的限流
- 分布式限流，使用Redis作为缓存

## 1. [计数器法]()

CountRateChecker

https://github.com/marcosbarbero/spring-cloud-zuul-ratelimit<br>

## 2. [漏桶算法](https://en.wikipedia.org/wiki/Leaky_bucket)
 
## 3. [令牌桶算法](https://en.wikipedia.org/wiki/Token_bucket)

TokenBucketRateChecker

https://github.com/UsedRarely/spring-rate-limit

## TO-DO

- 测试用例
- 压测