package com.pzhu.iacaa2_0.service.impl;

import com.pzhu.iacaa2_0.entity.CheckLink;
import com.pzhu.iacaa2_0.entityVo.CheckLinkVo;
import com.pzhu.iacaa2_0.mapper.CheckLinkMapper;
import com.pzhu.iacaa2_0.service.ICheckLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@Service
public class CheckLinkServiceImpl extends ServiceImpl<CheckLinkMapper, CheckLink> implements ICheckLinkService {

    @Override
    public List<CheckLink> list(CheckLinkVo vo) {
        return baseMapper.list(vo);
    }
}
