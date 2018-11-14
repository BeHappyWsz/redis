package wsz.redis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import redis.clients.jedis.Jedis;

/**
 * redis延迟队列
 * @author wsz
 * @date 2018年11月14日
 */
public class RedisDelayingQueue<T> {
	
	private String queueKey = "Queue";
	private Jedis jedis = new Jedis("127.0.0.1", 6379);
	//用于序列化
	private Type TaskType = new TypeReference<TaskItem<T>>(){}.getType();
	
	static class TaskItem<T>{
		public String id;
		public T msg;
	}
	
	/**
	 * 生产者：时间戳到期时间推后10s
	 * @param msg
	 */
	public void delay(T msg) {
		TaskItem<T> task = new TaskItem<>();
		task.id = UUID.randomUUID().toString().replace("-", "");
		task.msg = msg;
		jedis.zadd(queueKey, System.currentTimeMillis()+10000, JSON.toJSONString(task));
	}
	
	/**
	 * 消费者：多线程轮询获取到期的任务
	 */
	public void loop() {
		while(!Thread.interrupted()) {
			Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
			//数据为空，延时0.5s后继续获取
			if(values.isEmpty()) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				continue;
			}
			
			String next = values.iterator().next();
			//判断该线程是否抢到数据
			if(jedis.zrem(queueKey, next) > 0) {
				TaskItem<T> task = JSON.parseObject(next, TaskType);
				System.out.println(Thread.currentThread().getName()+":"+task.msg);
			}
		}
	}
	
	public static void main(String[] args) {
		final RedisDelayingQueue<String> queue = new RedisDelayingQueue<String>();
		
		//生产者线程
		final int num = 100;
		Thread producer = new Thread() {
			public void run() {
				for(int i =0; i< num; i++) {
					queue.delay("q"+i);
				}
			}
		};
		//消费者线程
		Thread consumer = new Thread() {
			public void run() {
				queue.loop();
			}
		};
		producer.start();
		consumer.start();
		try {
			producer.join();
			Thread.sleep(5000);
			consumer.interrupt();
			consumer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
