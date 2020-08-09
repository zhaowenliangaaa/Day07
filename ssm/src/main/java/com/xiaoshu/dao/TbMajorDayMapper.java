package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.TbMajorDay;
import com.xiaoshu.entity.TbMajorDayExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbMajorDayMapper extends BaseMapper<TbMajorDay> {
    long countByExample(TbMajorDayExample example);

    int deleteByExample(TbMajorDayExample example);

    List<TbMajorDay> selectByExample(TbMajorDayExample example);

    int updateByExampleSelective(@Param("record") TbMajorDay record, @Param("example") TbMajorDayExample example);

    int updateByExample(@Param("record") TbMajorDay record, @Param("example") TbMajorDayExample example);
}