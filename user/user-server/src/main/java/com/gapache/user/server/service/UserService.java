package com.gapache.user.server.service;

import com.gapache.commons.model.IPageRequest;
import com.gapache.commons.model.PageResult;
import com.gapache.user.common.model.vo.UserVO;

/**
 * @author HuSen
 * @since 2021/1/25 1:08 下午
 */
public interface UserService {

    UserVO create(UserVO vo);

    Boolean userIsExisted(Long id);

    UserVO findByUsername(String username, String clientId);

    boolean delete(Long id);

    UserVO get(Long id, String clientId);

    boolean update(UserVO vo);

    PageResult<UserVO> page(IPageRequest<UserVO> iPageRequest);
}
