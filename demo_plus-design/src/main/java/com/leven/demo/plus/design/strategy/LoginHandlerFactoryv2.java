package com.leven.demo.plus.design.strategy;

import com.lenven.demo.plus.common.LoginType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 通过spring的特性进行注入
 */

@Component
public class LoginHandlerFactoryv2 {
  private static final Map<LoginType, LoginHandler<Serializable>> LOGIN_HANDLER_MAP =
      new EnumMap<>(LoginType.class);

  public LoginHandlerFactoryv2(List<LoginHandler> handlers) {
    if (CollectionUtils.isEmpty(handlers)) {
      return;
    }
    for (LoginHandler handler : handlers) {
      LOGIN_HANDLER_MAP.put(handler.getLoginType(),handler);
    }
  }

  /**
   * 根据登录类型获取对应的处理器
   *
   * @param loginType 登录类型
   * @return 登录类型对应的处理器
   */
  public LoginHandler<Serializable> getHandler(LoginType loginType) {
    return LOGIN_HANDLER_MAP.get(loginType);
  }
}
