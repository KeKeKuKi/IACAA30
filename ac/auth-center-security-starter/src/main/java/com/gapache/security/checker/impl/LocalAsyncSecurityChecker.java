package com.gapache.security.checker.impl;

import com.alibaba.fastjson.JSON;
import com.gapache.security.checker.AsyncSecurityChecker;
import com.gapache.security.checker.SecurityChecker;
import com.gapache.security.interfaces.AsyncAuthorizeInfoManager;
import com.gapache.security.interfaces.AuthorizeInfoManager;
import com.gapache.security.model.AccessCard;
import com.gapache.security.model.AuthorizeInfoDTO;
import com.gapache.security.model.impl.CertificationImpl;
import com.gapache.security.utils.JwtUtils;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.HashSet;

/**
 * @author HuSen
 * @since 2020/7/31 12:45 下午
 */
@Slf4j
public class LocalAsyncSecurityChecker implements AsyncSecurityChecker {

    private final PublicKey publicKey;
    private final AsyncAuthorizeInfoManager asyncAuthorizeInfoManager;

    public LocalAsyncSecurityChecker(PublicKey publicKey, AsyncAuthorizeInfoManager asyncAuthorizeInfoManager) {
        this.publicKey = publicKey;
        this.asyncAuthorizeInfoManager = asyncAuthorizeInfoManager;
    }

    @Override
    public Future<AccessCard> checking(String token) {
        // 先解析并检查token
        return Future.future(event -> {
            try {
                String content = JwtUtils.parseToken(token, publicKey);
                log.info("parse token result:{}", content);
                if (content == null) {
                    event.complete(null);
                    return;
                }
                CertificationImpl certification = JSON.parseObject(content, CertificationImpl.class);
                Long uniqueId = certification.getUniqueId();
                String name = certification.getName();
                AccessCard accessCard = new AccessCard();
                accessCard.setUserId(uniqueId);
                accessCard.setUsername(name);
                accessCard.setClientId(certification.getClientId());
                accessCard.setSign(certification.getSign());

                asyncAuthorizeInfoManager.get(token)
                        .onSuccess(dto -> {
                            accessCard.setAuthorities(new HashSet<>(dto.getScopes()));
                            accessCard.setCustomerInfo(dto.getCustomerInfo());
                            event.complete(accessCard);
                        })
                        .onFailure(event::fail);


            } catch (Exception e) {
                log.error("check token error.", e);
                event.fail(e);
            }
        });
    }
}
