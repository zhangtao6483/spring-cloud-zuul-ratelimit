# spring-cloud-zuul-ratelimit

from: <br>
https://github.com/marcosbarbero/spring-cloud-zuul-ratelimit<br>
https://github.com/UsedRarely/spring-rate-limit

spring cloud zuul限流

限流算法

### 1. 计数器法
  实现CountRateLimit<br>
  计数器限流存在[临界问题](http://www.kissyu.org/2016/08/13/%E9%99%90%E6%B5%81%E7%AE%97%E6%B3%95%E6%80%BB%E7%BB%93/)：

![临界问题](https://raw.githubusercontent.com/zhangtao6483/spring-cloud-zuul-ratelimit/blob/master/img/count_problem.jpg)

用户在0:59时，瞬间发送了100个请求，并且1:00又瞬间发送了100个请求，那么其实这个用户在1秒里面，瞬间发送了200个请求。

使用Redis incr实现： http://redis.io/commands/incr

### 2. [漏桶算法](https://en.wikipedia.org/wiki/Leaky_bucket)
 
### 3. [令牌桶算法](https://en.wikipedia.org/wiki/Token_bucket)