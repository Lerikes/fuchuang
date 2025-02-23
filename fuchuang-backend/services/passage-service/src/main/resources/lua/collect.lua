--keys
--KEYS[1] 文章收藏set的键
--KEYS[2] 文章收藏数量的键
--KEYS[3] 文章作者收藏总数的键
--KEYS[4] 当前用户点赞的文章的集合的键

--values
--ARGV[1] 当前用户的id
--ARGV[2] 文章的id

-- 1.文章收藏
if (redis.call('sadd',KEYS[1],ARGV[1]) == 0 ) then
    redis.error_reply("already collect!")
end

-- 2.增加文章收藏数量
redis.call('incr',KEYS[2])

-- 3.增加文章作者收藏总数
redis.call('incr',KEYS[3])

-- 4.在当前用户的收藏的文章集合中增加
redis.call("sadd",KEYS[4],ARGV[2])