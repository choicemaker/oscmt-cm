package com.choicemaker.cms.webapp.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.choicemaker.cms.webapp.model.Message;

@Stateless
public class MessageService {

	@PersistenceContext
	private EntityManager entityManager;

	public void create(Message message) {
		entityManager.persist(message);
	}

	public List<Message> list() {
		TypedQuery<Message> q =
			entityManager.createQuery("FROM Message m", Message.class);
		List<Message> retVal = q.getResultList();
		return retVal;
	}

}
