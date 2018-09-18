package wsz.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
/**
 * 无序字典,值只能为字符串,数组 + 链表二维结构
 * 存在hash碰撞
 * 渐进式rehash:会在 rehash 的同时，保留新旧两个 hash 结构，查询时会同时查询两个hash结构
 * 				然后在后续的定时任务中以及 hash 操作指令中，循序渐进地将旧 hash 的内容一点点迁移到新的 hash 结构中。
 * 					当搬迁完成了，就会使用新的hash结构取而代之。
 * 当移除了最后一个元素之后，该数据结构自动被删除，内存被回收
 * @author wsz
 * @date 2018年9月18日
 */
public class RedisHash {

	Jedis redis = new Jedis("localhost", 6379);
	
	String KEY = "r_hash";
	
//	@Test
	public void set() {
		// 单hset
		redis.hset(KEY, "a", "a");
		redis.hset(KEY, "b", "b");
		redis.hset(KEY, "c", "c");
		Map<String, String> hgetAll = redis.hgetAll(KEY);
		System.out.println(hgetAll.toString());
		
		// 批量hmset
		hgetAll.put("d", "d");
		hgetAll.put("e", "e");
		hgetAll.put("f", "f");
		redis.hmset(KEY, hgetAll);
		System.out.println(hgetAll.toString());
		
		// Set the specified hash field to the specified value if the field not exists.
		redis.hsetnx(KEY, "g", "g");
		System.out.println(hgetAll);
	}
	
//	@Test
	public void get() {
		//单个获取
		String a = redis.hget(KEY, "a");
		System.out.println(a);
		String b = redis.hget(KEY, "b");
		System.out.println(b);
		
		//批量获取values,不存在未null
		List<String> hmget = redis.hmget(KEY, "a","b","c","qq");
		System.out.println(hmget);
		//获取所有key-values
		List<String> hvals = redis.hvals(KEY);
		Set<String> hkeys = redis.hkeys(KEY);
		System.out.println(hvals+""+hkeys);
		//获取长度
		Long hlen = redis.hlen(KEY);
		System.out.println(hlen);
		
		//判断是否存在
		Boolean q = redis.hexists(KEY, "q");
		System.out.println(q);
		Boolean hexists = redis.hexists(KEY, "a");
		System.out.println(hexists);
		
	}
	
//	@Test
	public void update() {
		redis.hset(KEY, "integer", "100");
		Long hincrBy = redis.hincrBy(KEY, "integer", -5);// 自增时string必须可转换为integer
		System.out.println(hincrBy);
	}
	
	@Test
	public void delete() {
		//批量根据key删除,当全部删除后key也被删除
		Long hdel = redis.hdel(KEY, "a", "b");
		System.out.println(hdel);
		//直接删除key
		Long del = redis.del(KEY);
		System.out.println(del);
	}
}
