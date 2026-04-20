package com.eims.system.service;

import com.eims.system.domain.vo.LoginVO;
import java.util.Map;

public interface ISysAuthService {
    Map<String, Object> login(LoginVO loginVO);
    void logout();
    Map<String, Object> getUserInfo();
}
