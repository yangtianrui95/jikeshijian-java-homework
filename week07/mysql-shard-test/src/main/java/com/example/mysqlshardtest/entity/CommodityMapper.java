package com.example.mysqlshardtest.entity;

import com.example.mysqlshardtest.entity.Commodity;
import com.example.mysqlshardtest.entity.CommodityExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommodityMapper {
    long countByExample(CommodityExample example);

    int deleteByExample(CommodityExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Commodity record);

    int insertSelective(Commodity record);

    Commodity selectOneByExample(CommodityExample example);

    Commodity selectOneByExampleSelective(@Param("example") CommodityExample example, @Param("selective") Commodity.Column ... selective);

    List<Commodity> selectByExampleSelective(@Param("example") CommodityExample example, @Param("selective") Commodity.Column ... selective);

    List<Commodity> selectByExample(CommodityExample example);

    Commodity selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") Commodity.Column ... selective);

    Commodity selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Commodity record, @Param("example") CommodityExample example);

    int updateByExample(@Param("record") Commodity record, @Param("example") CommodityExample example);

    int updateByPrimaryKeySelective(Commodity record);

    int updateByPrimaryKey(Commodity record);

    int batchInsert(@Param("list") List<Commodity> list);

    int batchInsertSelective(@Param("list") List<Commodity> list, @Param("selective") Commodity.Column ... selective);

    int upsert(Commodity record);

    int upsertSelective(Commodity record);
}