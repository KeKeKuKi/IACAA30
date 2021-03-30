package com.gapache.security.model.impl;

import com.gapache.security.model.Certification;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author HuSen
 * @since 2020/7/31 1:37 下午
 */
@Setter
@Getter
@ToString
public class CertificationImpl implements Certification {
    private static final long serialVersionUID = 511998569184094424L;

    private Long id;

    private String name;

    private String clientId;

    private Boolean sign;

    @Override
    public Long getUniqueId() {
        return this.id;
    }
}
