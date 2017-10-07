package com.file.sharing.core.dao.impl;

import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.file.sharing.core.dao.AbstractDaoImpl;
import com.file.sharing.core.dao.UsersDao;
import com.file.sharing.core.entity.User;

@Repository
public class UsersDaoImpl extends AbstractDaoImpl<User> implements UsersDao {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Optional<User> findByEmail(String email) {
		TypedQuery<User> query = entityManager.createNamedQuery("from User u where u.email=:email", User.class);
		query.setParameter("email", email);
		User user = null;
		try {
			user = (User) query.getSingleResult();
		} catch (NoResultException e) {
			return Optional.empty();
		} catch (NonUniqueResultException e) {
			logger.warn("More than one user found for email: " + email, e);
			return Optional.empty();
		}
		return Optional.of(user);
	}

}