package com.xiaoshu.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.TbMajorDayMapper;
import com.xiaoshu.dao.TbStuDayMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.TbMajorDay;
import com.xiaoshu.entity.TbStuDay;
import com.xiaoshu.entity.Tj;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class TbStuDayService {

	@Autowired
	TbStuDayMapper tsdm;
	
	@Autowired
	TbMajorDayMapper tmdm;
	
	@Autowired
	private JmsTemplate jt;

	//查询学生且分页的方法
	public PageInfo<TbStuDay> findTbStuDayPage(int pageNum, int pageSize,TbStuDay tsd) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbStuDay> stuList = tsdm.queryTbStuDay(tsd);
		PageInfo<TbStuDay> pageInfo = new PageInfo<TbStuDay>(stuList);
		return pageInfo;
	}
	
	//新增学生的方法，该方法来自于通用mapper
	public void addTbStuDay(TbStuDay tsd) {
		tsdm.insertSelective(tsd);
	}
	
	//查询全部科目的方法
	public List<TbMajorDay> querytmd(){
		return tmdm.selectAll();
	}
	
	//修改功能
	public void updateTbStuDay(TbStuDay tsd) {
		tsdm.updateByPrimaryKeySelective(tsd);
	}
	
	//删除功能
	public void deleteTbStuDay(Integer sdid) {
		tsdm.deleteByPrimaryKey(sdid);
	}
	
	//导出方法
	public List<TbStuDay> dc() {
		return tsdm.dc();
	}
	
	//报表方法
	public List<Tj> getTj(){
		return tsdm.getTj();
	}
	
	//添加新专业
	public void addZy(TbMajorDay tmd) {
		tmdm.insertSelective(tmd);
		fxx(JSON.toJSONString(tmd));
	}
	
	//发消息的方法
	public void fxx(final String xx) {
		//通过注入的消息管理对象进行消息发送
		//发送消息时，第一个参数为消息队列名称，第二个参数为消息对象
		jt.send("ssm_dl",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createTextMessage(xx);
			}
		});
	}


}
