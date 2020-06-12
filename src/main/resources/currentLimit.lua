-- lua 下标从 1 开始
-- 限流 key
local key = KEYS[1]
-- 限流大小
local limit = tonumber(ARGV[1])

-- 获取当前流量大小
local currentLimit = tonumber(redis.call('get', key) or "0")

if (limit + 10000 < currentLimit) then

    -- 达到限流大小超过限流大小10000，可能是有效期出了问题
    local ttlTime = redis.call('ttl',key)
    if (ttlTime == -1) then
        -- 如果发现有限期变成-1 设置有效期设置为2秒
        redis.call("EXPIRE", key, 2)
    end
    return 0;
elseif (currentLimit + 1 > limit) then

    -- 达到限流大小 返回
    return 0;
elseif (currentLimit == 0) then
    -- 没有达到阈值 value + 1 有效期设置为2秒
    redis.call("INCRBY", key, 1)
    redis.call("EXPIRE", key, 2)
    return currentLimit + 1
else
    -- 没有达到阈值 value + 1
    redis.call("INCRBY", key, 1)
    return currentLimit + 1
end

