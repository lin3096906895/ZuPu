package com.zupu.zupu.service;

import com.zupu.zupu.dto.TreeNodeVO;
import com.zupu.zupu.model.GraphVO;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface TreeService {
    GraphVO buildGraph(Integer familyId, LoginUser user);

    List<TreeNodeVO> getRoots(Integer familyId, LoginUser user);

    List<TreeNodeVO> getChildren(Integer personId, LoginUser user);
}
