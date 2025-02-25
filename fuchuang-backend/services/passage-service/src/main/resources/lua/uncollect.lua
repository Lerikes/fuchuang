--keys
--KEYS[1] 文章收藏set的键
--KEYS[2] 文章收藏数量的键
--KEYS[3] 文章作者收藏总数的键
--KEYS[4] 当前用户点赞的文章的集合的键

--values
--ARGV[1] 当前用户的id
--ARGV[2] 文章的id

-- 1. 删除集合中的用户
if redis.call('srem', KEYS[1], ARGV[1]) == 0 then
    redis.error_reply("already uncollect!")
end

-- 2. 安全减少文章收藏数,防止出现小于0的情况
local count = tonumber(redis.call('get', KEYS[2])) or 0
if count > 0 then
    redis.call('set', KEYS[2], count - 1)  -- 使用计算后的值直接设置
else
    redis.call('set', KEYS[2], 0)          -- 确保不会出现负数
end

-- 3. 安全减少作者收藏总数
local authorTotal = tonumber(redis.call('get', KEYS[3])) or 0
if authorTotal > 0 then
    redis.call('set', KEYS[3], authorTotal - 1)
else
    redis.call('set', KEYS[3], 0)
end

-- 4. 从用户收藏集合中删除文章
redis.call('srem', KEYS[4], ARGV[2])

return 1