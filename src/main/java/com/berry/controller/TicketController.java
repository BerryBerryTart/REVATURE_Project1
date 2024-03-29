package com.berry.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.berry.DTO.CreateTicketDTO;
import com.berry.DTO.TicketStatusDTO;
import com.berry.model.Reimbursement;
import com.berry.model.Role;
import com.berry.model.RoleEnum;
import com.berry.model.Users;
import com.berry.service.TicketService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class TicketController implements Controller  {
	private TicketService ticketService;
	
	public TicketController() {
		this.ticketService = new TicketService();
	}
	
	private Handler addTicketHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			Reimbursement ticket = null;
			CreateTicketDTO createTicketDTO = new CreateTicketDTO();
			
			createTicketDTO = ctx.bodyAsClass(CreateTicketDTO.class);
			
			ticket = ticketService.CreateTicket(user, createTicketDTO);
			
			if (ticket != null) {
				ctx.json(ticket);
				ctx.status(201);
			}
			
		}
		
	};
	
	private Handler getAllTicketsHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			List<Reimbursement> tickets = new ArrayList<Reimbursement>();
			
			tickets = ticketService.getAllUserTickets(user);
			Map<String, List<Reimbursement>> jsonMap = new HashMap<String, List<Reimbursement>>();
			jsonMap.put("Reimbursements", tickets);
			
			ctx.json(jsonMap);
			ctx.status(200);
		}
	};
	
	private Handler getTicketByIdHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			Reimbursement ticket = null;
			String stringTicketID = ctx.pathParam("id");
			
			ticket = ticketService.getUserTicketById(user, stringTicketID);
			
			if (ticket != null) {
				ctx.json(ticket);
				ctx.status(201);
			}
		}
	};
	
	//ADMIN PRIVS
	
	private Handler adminViewAllTicketsHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			//ROLE CHECK
			Role role = user.getRole_id();
			
			if (role.getRole().equals(RoleEnum.MANAGER.toString()) == false) {
				ctx.json(ResponseMap.getResMap("Error", "You Do Not Have Permission To Access This Page"));
				ctx.status(403);
			} else {
				List<Reimbursement> tickets = new ArrayList<Reimbursement>();
				
				tickets = ticketService.getAllAdminTickets();
				Map<String, List<Reimbursement>> jsonMap = new HashMap<String, List<Reimbursement>>();
				jsonMap.put("Reimbursements", tickets);
				
				ctx.json(jsonMap);
				ctx.status(200);
			}
		}
	};
	
	private Handler adminViewTicketByIdHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			//ROLE CHECK
			Role role = user.getRole_id();
			
			if (role.getRole().equals(RoleEnum.MANAGER.toString()) == false) {
				ctx.json(ResponseMap.getResMap("Error", "You Do Not Have Permission To Access This Page"));
				ctx.status(403);
			} else {
				Reimbursement ticket = null;
				String stringTicketID = ctx.pathParam("id");
				
				ticket = ticketService.getAdminTicketById(stringTicketID);
				
				if (ticket != null) {
					ctx.json(ticket);
					ctx.status(201);
				}
			}
		}
	};
	
	private Handler adminUpdateTicketHandler = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			//ROLE CHECK
			Role role = user.getRole_id();
			
			if (role.getRole().equals(RoleEnum.MANAGER.toString()) == false) {
				ctx.json(ResponseMap.getResMap("Error", "You Do Not Have Permission To Access This Page"));
				ctx.status(403);
			} else {
				Reimbursement ticket = null;
				String stringTicketID = ctx.pathParam("id");
			
				TicketStatusDTO ticketStatusDTO = ctx.bodyAsClass(TicketStatusDTO.class);
				
				ticket = ticketService.updateAdminTicketById(stringTicketID, user, ticketStatusDTO);
				
				if (ticket != null) {
					ctx.json(ticket);
					ctx.status(201);
				}
			}
		}
	};
	
	private Handler fetchTicketBlob = (ctx) -> {
		Users user = (Users) ctx.sessionAttribute("currentlyLoggedInUser");
		
		if (user == null) {
			ctx.json(ResponseMap.getResMap("Error", "User Is Not Logged In."));
			ctx.status(400);
		} else {
			String stringTicketID = ctx.pathParam("id");
			byte[] ticketBlob = null;
			
			ticketBlob = ticketService.fetchTicketBlob(user, stringTicketID);
			
			if (ticketBlob != null) {
				ctx.json(ticketBlob);
				ctx.status(200);
			}
		}
	};
	
	public void mapEndpoints(Javalin app) {
		//USER PRIVS
		app.post("/add_ticket", addTicketHandler);
		app.get("/get_ticket_status/", getAllTicketsHandler);
		app.get("/get_ticket_status/:id", getTicketByIdHandler);
		
		//ADMIN PRIVS
		app.get("/view_ticket", adminViewAllTicketsHandler);
		app.get("/view_ticket/:id", adminViewTicketByIdHandler);
		app.put("/update_ticket/:id", adminUpdateTicketHandler);
		
		//SELECTIVE PRIVS
		app.get("/ticket_blob/:id", fetchTicketBlob);
	}
}
