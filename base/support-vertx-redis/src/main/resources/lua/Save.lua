-- 数据的id
local id = KEYS[1]
-- 数据的长度 -3是因为最后3个是key、当前时间戳、id
local valueLength = table.getn(ARGV)
for i = 1, valueLength - 3 do
    local key = KEYS[i + 1]
    local value = ARGV[i]
    redis.call('HSET', id, key, value)
end
redis.call('ZADD', 'PAGE:' .. ARGV[valueLength - 2], ARGV[valueLength - 1], ARGV[valueLength])
return 'OK'