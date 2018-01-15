package cn.itcast.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.core.Constans;
import cn.itcast.core.dao.user.BuyerDao;
import cn.itcast.core.pojo.user.Buyer;
import cn.itcast.core.pojo.user.BuyerQuery;
import cn.itcast.core.pojo.user.BuyerQuery.Criteria;
import redis.clients.jedis.Jedis;

@Service("loginServiceImpl")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private BuyerDao buyerDao;
	@Autowired
	private Jedis jedis;
	
	@Override
	public Buyer findBuyerByUserName(String userName) {
		BuyerQuery buyerQuery = new BuyerQuery();
		Criteria createCriteria = buyerQuery.createCriteria();
		createCriteria.andUsernameEqualTo(userName);
		
		List<Buyer> buyerList = buyerDao.selectByExample(buyerQuery);
		if (buyerList!=null&&buyerList.size()>0) {
			return buyerList.get(0);
			
		}
		return null;
	}

	@Override
	public void setUserNameToRedis(String token, String UserName) {
		jedis.set(token+Constans.USERNAME_KEY, UserName);
		//设置存储时间为30分钟
		jedis.expire(token+Constans.USERNAME_KEY, 60*30);
		
	}

	@Override
	public String findUserNameFromRedis(String token) {
		String username = jedis.get(token+Constans.USERNAME_KEY);
		if (username!=null&&!"".equals(username)) {
			//获取登录信息的时候从新刷新用户的超时时间, 防止登录超时
			jedis.expire(token+Constans.USERNAME_KEY, 60*30);
		}
		
		return username;
	}

	
}
