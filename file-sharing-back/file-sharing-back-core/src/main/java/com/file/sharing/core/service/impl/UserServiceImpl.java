package com.file.sharing.core.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.file.sharing.core.business.UserBusiness;
import com.file.sharing.core.exception.UserNotFoundException;
import com.file.sharing.core.objects.UserInfo;
import com.file.sharing.core.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private UserBusiness userBusiness;

	@Override
	public UserInfo getUserInfoByEmail(String email) {
		Optional<UserInfo> userInfo = userBusiness.getUserInfoByEmail(email);
		if (!userInfo.isPresent()) {
			throw new UserNotFoundException(email);
		}
		return userInfo.get();
	}

	@Override
	public UserInfo getUserInfoByUserId(Integer userId) {
		Optional<UserInfo> userInfo = userBusiness.getUserInfoByUserId(userId);
		if (!userInfo.isPresent()) {
			throw new UserNotFoundException(userId);
		}
		return userInfo.get();
	}


}