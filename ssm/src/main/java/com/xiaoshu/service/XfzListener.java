package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.entity.TbMajorDay;

public class XfzListener implements MessageListener{
	
	@Autowired
	private RedisTemplate rt;

	@Override
	public void onMessage(Message message) {
		TextMessage tm=(TextMessage) message;
		String xx;
		try {
			xx = tm.getText();
			TbMajorDay tmd=JSONObject.parseObject(xx, TbMajorDay.class);
			System.out.println("新专业名称"+tmd.getMdname());
			rt.boundValueOps(tmd.getMdname()).set("测试数据");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
