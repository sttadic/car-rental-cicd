package edu.stjepan.carrental.mapper;

import edu.stjepan.carrental.dto.CarDTO;
import edu.stjepan.carrental.entity.Car;

public class CarMapper {

	public static CarDTO toDTO(Car car) {
		return new CarDTO(car.getId(), car.getMake(), car.getModel(), car.getRegistrationNumber(), car.getYear(),
				car.getDailyRate(), car.getBookings().size());
	}
}
