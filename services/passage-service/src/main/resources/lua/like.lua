--keys
--KEYS[1] 文章点赞set的键
--KEYS[2] 文章点赞数量的键
--KEYS[3] 文章作者点赞总数的键
--KEYS[4] 当前用户点赞的文章的集合的键

--values
--ARGV[1] 当前用户的id
--ARGV[2] 文章的id

-- 1.视频点赞，如果已经点赞就报错，用bitmap存储
-- setbit 返回值是原来存储位的值
if (redis.call('setbit',KEYS[1],ARGV[1],1) == 1 ) then
    redis.error_reply("already like!")
end

-- 2.增加视频点赞数量
redis.call('incr',KEYS[2])

-- 3.增加视频作者点赞总数
redis.call('incr',KEYS[3])

-- 4.在用户点赞视频的集合的键
redis.call('sadd',KEYS[4],ARGV[2])