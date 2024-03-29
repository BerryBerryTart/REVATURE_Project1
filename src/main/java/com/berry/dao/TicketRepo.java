package com.berry.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berry.DTO.CreateTicketDTO;
import com.berry.DTO.TicketStatusDTO;
import com.berry.app.Application;
import com.berry.exception.NotFoundException;
import com.berry.model.Reimbursement;
import com.berry.model.Status;
import com.berry.model.StatusEnum;
import com.berry.model.Type;
import com.berry.model.TypeEnum;
import com.berry.model.Users;
import com.berry.util.SessionUtility;

public class TicketRepo {
	private static Logger logger = LoggerFactory.getLogger(Application.class);
	
	public Reimbursement createTicket(Users user, CreateTicketDTO createTicketDTO){
		Session session = SessionUtility.getSession();
		session.beginTransaction();
		
		Reimbursement ticket = new Reimbursement();
		
		try {
			TypeEnum typeEnum = TypeEnum.getEnum(createTicketDTO.getType());
			Type type = session.load(Type.class, typeEnum.getIndex());
			Status status = session.load(Status.class, StatusEnum.PENDING.getIndex());
			
			ticket.setAmount(createTicketDTO.getAmount());
			ticket.setDescription(createTicketDTO.getDescription());
			ticket.setReceipt(createTicketDTO.getReceipt());
			ticket.setType_id(type);
			ticket.setAuthor(user);
			ticket.setStatus_id(status);
			
			session.save(ticket);
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}		
		
		return ticket;
	}

	public Reimbursement getUserTicketById(Users user, int id) throws NotFoundException {
		Session session = SessionUtility.getSession();
		
		Reimbursement ticket = new Reimbursement();
		String hql = "FROM Reimbursement r WHERE r.author_id = :authorid AND r.reimb_id = :reimbid";
	
		try {
			ticket = (Reimbursement) session.createQuery(hql)
					.setParameter("authorid", user)
					.setParameter("reimbid", id)
					.getSingleResult();
		} catch (NoResultException e) {
			throw new NotFoundException("Ticket Not Found");
		} finally {
			session.close();
		}
		
		return ticket;
	}

	public List<Reimbursement> getAllUserTickets(Users user) throws NotFoundException {
		List<Reimbursement> tickets = new ArrayList<Reimbursement>();
		Session session = SessionUtility.getSession();
		
		String hql = "FROM Reimbursement r WHERE r.author_id = :authorid";
		
		try {
			tickets = session.createQuery(hql, Reimbursement.class).setParameter("authorid", user).getResultList();
		} catch (NoResultException e) {
			throw new NotFoundException("Ticket Not Found");
		} finally {
			session.close();
		}
		
		return tickets;
	}

	public List<Reimbursement> getAllAdminTickets() {
		List<Reimbursement> tickets = new ArrayList<Reimbursement>();
		Session session = SessionUtility.getSession();
		
		String hql = "FROM Reimbursement r";
		
		tickets = session.createQuery(hql, Reimbursement.class).getResultList();
		
		return tickets;
	}

	public Reimbursement getAdminTicketById(int id) throws NotFoundException {
		Session session = SessionUtility.getSession();
		
		Reimbursement ticket = new Reimbursement();
		String hql = "FROM Reimbursement r WHERE r.reimb_id = :reimbid";
	
		try {
			ticket = (Reimbursement) session.createQuery(hql)
					.setParameter("reimbid", id)
					.getSingleResult();
		} catch (NoResultException e) {
			throw new NotFoundException("Ticket Not Found");
		} finally {
			session.close();
		}
		
		return ticket;
	}

	public Reimbursement updateAdminTicketById(int id, Users user, TicketStatusDTO ticketStatusDTO) throws NotFoundException {
		Session session = SessionUtility.getSession();
		session.beginTransaction();
		
		Reimbursement ticket = new Reimbursement();
		Date now = new Date();
		StatusEnum statusEnum = StatusEnum.getStatusFromString(ticketStatusDTO.getStatus());
		
		String hql = "UPDATE Reimbursement r SET r.resolver_id=:resolver_id, r.status_id=:status_id, "
				+ "r.resolved=:now WHERE r.reimb_id = :reimbid";		
		
		Status status = session.load(Status.class, statusEnum.getIndex());
		int result = session.createQuery(hql)
				.setParameter("status_id", status)
				.setParameter("resolver_id", user)
				.setParameter("now", now)
				.setParameter("reimbid", id).executeUpdate();
	
		session.getTransaction().commit();		
		if (result == 0) {
			throw new NotFoundException("Ticket Not Found");
		}		

		ticket = session.get(Reimbursement.class, id);
		
		session.close();
		return ticket;
	}

	public byte[] fetchTicketBlob(Users user, int id) throws NotFoundException {
		byte[] ticketBlob = null;
		Session session = SessionUtility.getSession();
		
		String hql = "SELECT r.receipt from Reimbursement r WHERE r.reimb_id = :reimbid";
		
		if (user.getRole_id().getRole().equals("EMPLOYEE")) {
			hql = hql.concat(" AND r.author_id = :authorid");
			
			try {
				ticketBlob = (byte[]) session.createQuery(hql)
						.setParameter("reimbid", id)
						.setParameter("authorid", user)
						.getSingleResult();
			} catch (NoResultException e) {
				throw new NotFoundException("Ticket Not Found");
			} finally {
				session.close();
			}
		} else {
			try {
				ticketBlob = (byte[]) session.createQuery(hql)
						.setParameter("reimbid", id)
						.getSingleResult();
			} catch (NoResultException e) {
				throw new NotFoundException("Ticket Not Found");
			} finally {
				session.close();
			}
		}
		return ticketBlob;
	}

}
