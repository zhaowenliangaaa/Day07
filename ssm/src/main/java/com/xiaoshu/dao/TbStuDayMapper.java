package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.TbStuDay;
import com.xiaoshu.entity.TbStuDayExample;
import com.xiaoshu.entity.Tj;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbStuDayMapper extends BaseMapper<TbStuDay> {
    public List<TbStuDay> queryTbStuDay(TbStuDay tsd);
    
    public List<TbStuDay> dc();
    
    public List<Tj> getTj();
    
    public void addStu(TbStuDay tsd);
}