package edu.stjepan.carrental.mapper;

import edu.stjepan.carrental.dto.BookingDTO;
import edu.stjepan.carrental.entity.Booking;

public class BookingMapper {

	public static BookingDTO toDTO(Booking booking) {
		return new BookingDTO(booking.getId(), booking.getCar().getId(), booking.getCustomerName(),
				booking.getCustomerEmail(), booking.getStartDate(), booking.getEndDate(), booking.getTotalAmount(),
				booking.getStatus());
	}
}
