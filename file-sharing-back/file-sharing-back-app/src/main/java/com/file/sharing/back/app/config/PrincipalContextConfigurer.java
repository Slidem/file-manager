package com.file.sharing.back.app.config;

import com.file.sharing.core.caching.impl.StorageInfoCache;
import com.file.sharing.core.caching.impl.UserInfoCache;
import com.file.sharing.core.objects.UserInfo;
import com.file.sharing.core.service.StorageService;
import com.file.sharing.core.service.UserService;
import com.file.sharing.rest.context.ContextConfigurer;
import com.file.sharing.rest.context.ContextImpl.ContextBuidler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Alexandru Mihai
 * @created Oct 29, 2017
 */
@Component
public class PrincipalContextConfigurer implements ContextConfigurer {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserService userService;
	private StorageService storageService;
	private StorageInfoCache storageInfoCache;
	private UserInfoCache userInfoCache;

	@Autowired
	public PrincipalContextConfigurer(UserService userService, StorageService storageService, StorageInfoCache storageInfoCache, UserInfoCache userInfoCache) {
		this.userService = userService;
		this.storageService = storageService;
		this.storageInfoCache = storageInfoCache;
		this.userInfoCache = userInfoCache;
	}

	@Override
	public void configure(ContextBuidler builder) {

		logger.info("Configuring context through: {}", getClass().getName());

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email = (String) principal;
		builder.setUserEmail(email);

		UserInfo userInfo = userInfoCache.get(email);

		builder.setUserId(userInfo.getUserId());
		builder.setUserSubscription(userInfo.getAccStatsInfo().getSubscription());
		builder.setUserStorageInfo(storageInfoCache.get(userInfo.getUserId()));
	}

}
