package com.gapache.security.core;

import com.gapache.security.interfaces.AsyncAuthorizeInfoManager;
import com.gapache.security.interfaces.AuthorizeInfoManager;
import com.gapache.security.model.CustomerInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HuSen
 * @since 2020/8/4 6:19 下午
 */
@Slf4j
public class RedisAuthorizeInfoManager implements AuthorizeInfoManager {

    private final AsyncAuthorizeInfoManager asyncAuthorizeInfoManager;

    public RedisAuthorizeInfoManager(AsyncAuthorizeInfoManager asyncAuthorizeInfoManager) {
        this.asyncAuthorizeInfoManager = asyncAuthorizeInfoManager;
    }

    @Override
    public void save(String token, Long timeout, CustomerInfo customerInfo, Collection<String> scopes) {
        AtomicBoolean wait = new AtomicBoolean(false);
        asyncAuthorizeInfoManager.save(token, timeout, customerInfo, scopes)
                .onComplete(as -> {
                    wait.compareAndSet(false, true);
                    if (!as.succeeded()) {
                        log.error("save fail.", as.cause());
                    }
                });

        while (!wait.get()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void delete(String token) {
        AtomicBoolean wait = new AtomicBoolean(false);
        asyncAuthorizeInfoManager.delete(token)
                .onComplete(as -> {
                    wait.compareAndSet(false, true);
                    if (!as.succeeded()) {
                        log.error("delete fail.", as.cause());
                    }
                });

        while (!wait.get()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public String get(String token) {
        // TODO 暂时没用到，不用实现
        return null;
    }
}
