package wsz.redis;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTest {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Jedis r = new Jedis("localhost");
		r.auth("root");//密码
		
		r.set("str", "存储字符串");
		System.out.println(r.get("str"));
		
		r.lpush("set-list", "a");
		r.lpush("set-list", "b");
		r.lpush("set-list", "c");
		r.lpush("set-list", "d");
		List<String> list = r.lrange("set-list", 0, 3);
		for (String str : list) {
			System.out.print(str+" ");
		}
		System.out.println();
		
		//获取所有key
		Set<String> keys = r.keys("*");
		for (String key : keys) {
			System.out.println(key);
		}
	}
}
