package com.Sucat.domain.notify.aop.proxy;

import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.user.model.User;

public interface NotifyInfo {
    User getReceiver();

    Long getGoUrlId();

    NotifyType getNotifyType();
}
