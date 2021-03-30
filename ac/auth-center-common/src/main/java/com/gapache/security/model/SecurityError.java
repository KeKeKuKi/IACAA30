package com.gapache.security.model;

import com.gapache.commons.model.Error;
import lombok.Getter;

/**
 * @author HuSen
 * @since 2020/7/31 2:22 下午
 */
@Getter
public enum SecurityError implements Error {
    //
    LOGIN_FAIL(10403, "username or password wrong"),
    ERROR_CODE(10503, "mode of authorization_code's code invalid"),
    CLIENT_ERROR(10504, "client auth error"),
    NOT_SUPPORT(10505, "not support"),
    CLIENT_EXISTED(10506, "client id was existed"),
    USER_EXISTED(10507, "username was existed"),
    USER_CLIENT_RELATION_EXISTED(10508, "user client relation was existed"),
    NEED_REFRESH_TOKEN(10509, "need refresh token"),
    REFRESH_TOKEN_EXPIRED(10510, "refresh token expired"),
    PLEASE_LOGIN(10511, "please login"),
    INVALID_PARAMS(10512, "invalid params"),
    FORBIDDEN(10513, "forbidden"),
    INVALID_TOKEN(10514, "invalid token"),
    SIGN_INVALID(10515, "invalid sign"),
    ROLE_EXISTED(10516, "role existed"),
    ROLE_NOT_FOUND(10517, "role not found"),
    USER_NOT_FOUND(10518, "user not found");

    SecurityError(Integer code, String error) {
        this.code = code;
        this.error = error;
    }

    private final Integer code;
    private final String error;
}
