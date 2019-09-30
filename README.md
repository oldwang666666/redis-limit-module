# redis-limit-module
一个用redis配合注解达到限流目的maven工程

## 初始化
``` 
@Configuration
@PropertySource("classpath:config/redis.properties")
@ComponentScan(value = "com.wang.limit.intercept")
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.block-when-exhausted}")
    private boolean  blockWhenExhausted;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${redis.limit}")
    private int limit;

    @Autowired
    JedisPool jedisPool;

    @Bean
    public JedisPool redisPoolFactory()  throws Exception{
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,host,port,timeout,null);

        log.info("JedisPool注入成功！");
        log.info("redis地址：" + host + ":" + port);
        return  jedisPool;
    }

    @Bean
    public RedisCurrentLimit build() {
        RedisCurrentLimitFactory redisCurrentLimitFactory = new RedisCurrentLimitFactory();
        RedisCurrentLimit currentLimit = null;
        try {
            currentLimit = redisCurrentLimitFactory.standAloneInstance(limit, jedisPool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentLimit;
    }
}
```

## 使用ControllerLimit注解 - 拦截器
``` 
    @ControllerLimit(limit = 3,errorCode = 200,errorMessage = "自定义错误")
    @RequestMapping("/test")
    public void test(Long id){

    }
```

## 使用AspectLimit注解 - 切面
``` 
    @AspectLimit
    @Override
    public void test(){}
```

