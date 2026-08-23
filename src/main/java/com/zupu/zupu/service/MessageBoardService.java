package com.zupu.zupu.service;

import com.zupu.zupu.entity.MessageBoard;
import com.zupu.zupu.mapper.MessageBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageBoardService {
    
    @Autowired
    private MessageBoardMapper messageBoardMapper;
    
    public List<MessageBoard> listAll() {
        return messageBoardMapper.selectAll();
    }
    
    public MessageBoard getById(Integer id) {
        return messageBoardMapper.selectById(id);
    }
    
    public void create(MessageBoard messageBoard) {
        messageBoardMapper.insert(messageBoard);
    }
    
    public void update(MessageBoard messageBoard) {
        messageBoardMapper.update(messageBoard);
    }
    
    public void delete(Integer id) {
        messageBoardMapper.deleteById(id);
    }
    
    public int count() {
        return messageBoardMapper.count();
    }
    
    public List<MessageBoard> listByPage(int offset, int limit) {
        return messageBoardMapper.selectByPage(offset, limit);
    }
}
