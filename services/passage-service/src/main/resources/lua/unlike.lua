--keys
--KEYS[1] 文章收藏set的键
--KEYS[2] 文章收藏数量的键
--KEYS[3] 文章作者收藏总数的键
--KEYS[4] 当前用户点赞的文章的集合的键

--values
--ARGV[1] 当前用户的id
--ARGV[2] 文章的id

-- 1.删除数据，利用bitmap做优化
-- setbit 返回值是原来存储位的值
if (redis.call('setbit',KEYS[1],ARGV[1],0) == 0) then
    redis.error_reply("already unlike")
end

-- 2.减少文章点赞数量
redis.call('decr',KEYS[2])

-- 3.减少文章作者点赞总数
redis.call('decr',KEYS[3])

-- 4.从当前用户点赞的文章集合中删除
redis.call("srem",KEYS[4],ARGV[2])