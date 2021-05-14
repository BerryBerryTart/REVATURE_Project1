package com.test.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.berry.DTO.TicketStatusDTO;
import com.berry.dao.TicketRepo;
import com.berry.exception.BadParameterException;
import com.berry.exception.ImproperTypeException;
import com.berry.exception.NotFoundException;
import com.berry.exception.ServiceLayerException;
import com.berry.model.Reimbursement;
import com.berry.model.Status;
import com.berry.model.StatusEnum;
import com.berry.model.Type;
import com.berry.model.TypeEnum;
import com.berry.model.Users;
import com.berry.service.TicketService;
import com.berry.util.SessionUtility;

public class testTicketServiceGetUserTicketById {
	private static Session mockSession;
	private static TicketRepo mockTicketRepo;

	private TicketService ticketService = new TicketService(mockTicketRepo);

	// one pixel base 64 image of one pixel. just for brevity
	private static final String blobString = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVQImWP4//8/AAX+Av5Y8msOAAAAAElFTkSuQmCC";
	private static final Users user1 = new Users();

	@Before
	public void beforeTest() {
		ticketService = new TicketService(mockTicketRepo);
	}

	@BeforeClass
	public static void setUp() throws Exception {
		mockTicketRepo = mock(TicketRepo.class);
		mockSession = mock(Session.class);

		Reimbursement ticket1 = new Reimbursement();
		ticket1.setAmount(500.30);
		ticket1.setDescription("Description");
		ticket1.setType_id(new Type(TypeEnum.BUISNESS.getIndex(), TypeEnum.BUISNESS.toString()));
		ticket1.setReceipt(Base64Conversion.base64toByteArray(blobString));
		ticket1.setStatus_id(new Status(StatusEnum.COMPLETED.getIndex(), StatusEnum.COMPLETED.toString()));

		when(mockTicketRepo.getUserTicketById(eq(user1), eq(1))).thenReturn(ticket1);

	}

	@Test
	public void testSuccessfulGetTicketByID() throws BadParameterException, ServiceLayerException, NotFoundException {
		try (MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSession).thenReturn(mockSession);

			Reimbursement expected = new Reimbursement();
			expected.setAmount(500.30);
			expected.setDescription("Description");
			expected.setType_id(new Type(TypeEnum.BUISNESS.getIndex(), TypeEnum.BUISNESS.toString()));
			expected.setReceipt(Base64Conversion.base64toByteArray(blobString));
			expected.setStatus_id(new Status(StatusEnum.COMPLETED.getIndex(), StatusEnum.COMPLETED.toString()));

			Reimbursement actual = ticketService.getUserTicketById(user1, "1");

			assertEquals(actual, expected);
		}
	}

	@Test(expected = BadParameterException.class)
	public void testSuccessfulGetTicketByIDInvalidParam()
			throws BadParameterException, ServiceLayerException, NotFoundException {
		try (MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSession).thenReturn(mockSession);

			ticketService.getUserTicketById(user1, "one");
		}
	}

	@Test(expected = BadParameterException.class)
	public void testSuccessfulGetTicketByIDEmptyParam()
			throws BadParameterException, ServiceLayerException, NotFoundException {
		try (MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSession).thenReturn(mockSession);
			
			ticketService.getUserTicketById(user1, "");
		}
	}

}
