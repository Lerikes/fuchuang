-- KEYS[1] 用户信息的key
-- KEYS[2] 要删除的验证码的key
-- ARGV[1] 用户信息
-- ARGV[2] 过期时间(单位：秒)

redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2])
redis.call('del', KEYS[2])
return 1