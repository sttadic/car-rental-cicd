package edu.stjepan.carrental.service;

import org.springframework.data.domain.*;

import edu.stjepan.carrental.dto.*;

public interface CarService {

	CarDTO createCar(CreateCarRequest request);

	CarDTO getCarById(Long id);

	Page<CarDTO> getAllCars(Pageable pageable);

	void deleteCar(Long id);
}
