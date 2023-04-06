package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.StockLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    int deleteByPrimaryKey(String stockLogId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    int insert(StockLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    int insertSelective(StockLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    StockLog selectByPrimaryKey(String stockLogId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    int updateByPrimaryKeySelective(StockLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Tue Apr 04 21:36:57 CST 2023
     */
    int updateByPrimaryKey(StockLog record);
}