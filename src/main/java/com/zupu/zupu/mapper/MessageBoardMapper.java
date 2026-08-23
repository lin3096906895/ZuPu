package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.MessageBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageBoardMapper {
    List<MessageBoard> selectAll();
    
    MessageBoard selectById(@Param("id") Integer id);
    
    int insert(MessageBoard messageBoard);
    
    int update(MessageBoard messageBoard);
    
    int deleteById(@Param("id") Integer id);
    
    int count();
    
    List<MessageBoard> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
}
