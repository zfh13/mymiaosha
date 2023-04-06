package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.SequenceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SequenceDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    int deleteByPrimaryKey(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    int insert(SequenceDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    int insertSelective(SequenceDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    SequenceDO selectByPrimaryKey(String name);

    SequenceDO getSequenceByName(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    int updateByPrimaryKeySelective(SequenceDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Sun Apr 02 13:49:47 CST 2023
     */
    int updateByPrimaryKey(SequenceDO record);
}