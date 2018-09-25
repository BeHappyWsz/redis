package wsz.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
/**
 * set，保证内部value唯一；每一个value赋予score，代表排序权重。跳跃列表
 * zset移除清空后，跳跃列表将自动删除内存被回收。
 * 
 * @author wsz
 * @date 2018年9月19日
 */
public class RedisZset {
	
	Jedis redis = new Jedis("localhost", 6379);

	String KEY = "r_zset";
	
//	@Test
	public void add() {
		//单add
		redis.zadd(KEY, 10, "a");
		redis.zadd(KEY, 20, "b");
		redis.zadd(KEY, 15, "c");
		//批量增加value-score
		Map<String, Double> scoreMembers = new HashMap<String, Double>(16);
		scoreMembers.put("d", 5D);
		scoreMembers.put("e", 5D);
		redis.zadd(KEY, scoreMembers);
	}
	
	// d 5
	// e 5
	// a 10
	// c 15
	// b 20
	
//	@Test
	public void get() {
		//根据value获取对应的score
		Double zscore = redis.zscore(KEY, "a");
		System.out.println(zscore);
		//获取value对应的排序0开始
		Long zrank = redis.zrank(KEY, "b");
		System.out.println(zrank);
		//统计value数量
		Long zcard = redis.zcard(KEY);
		System.out.println(zcard);
		
		//按score排序列出value，参数为排名范围deac
		Set<String> zrange = redis.zrange(KEY, 0, -2);
		System.out.println(zrange);
		//按score逆序列出value，参数为排名范围 eac -> cae
		Set<String> zrevrange = redis.zrevrange(KEY, 1, -2);
		System.out.println(zrevrange);
		
		//根据score取值区间[m,n]获取对应的value
		Set<String> zrangeByScore = redis.zrangeByScore(KEY, 5, 15);
		System.out.println(zrangeByScore);
		//根据分值区间 [0, 15] 遍历 zset，同时返回分值[[[100],5.0], [[101],5.0], [[97],10.0], [[99],15.0]]
		Set<Tuple> zrangeByScoreWithScores = redis.zrangeByScoreWithScores(KEY, 0, 15);
		System.out.println(zrangeByScoreWithScores);
		
	}
	
	@Test
	public void update() {
		//根据value更新对应score的值
		Double zincrby = redis.zincrby(KEY, 100, "e");
		System.out.println(zincrby);
	}
}
