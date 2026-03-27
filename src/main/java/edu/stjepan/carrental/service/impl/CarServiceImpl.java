package edu.stjepan.carrental.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import edu.stjepan.carrental.dto.*;
import edu.stjepan.carrental.entity.Car;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.mapper.CarMapper;
import edu.stjepan.carrental.repository.CarRepository;
import edu.stjepan.carrental.service.CarService;

@Service
public class CarServiceImpl implements CarService {
	private final CarRepository carRepository;

	@Autowired
	public CarServiceImpl(CarRepository carRepository) {
		this.carRepository = carRepository;
	}

	@Override
	public CarDTO createCar(CreateCarRequest request) {
		if (carRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
			throw new IllegalArgumentException("Car with this registration number already exists");
		}

		Car car = new Car(request.getMake(), request.getModel(), request.getRegistrationNumber(), request.getYear(),
				request.getDailyRate() != null ? request.getDailyRate() : BigDecimal.ZERO);

		Car saved = carRepository.save(car);
		return CarMapper.toDTO(saved);
	}

	@Override
	public CarDTO getCarById(Long id) {
		Car car = carRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

		return CarMapper.toDTO(car);
	}

	@Override
	public Page<CarDTO> getAllCars(Pageable pageable) {
		return carRepository.findAll(pageable).map(CarMapper::toDTO);
	}

	@Override
	public void deleteCar(Long id) {
		if (!carRepository.existsById(id)) {
			throw new ResourceNotFoundException("Car not found with id: " + id);
		}
		carRepository.deleteById(id);

	}

}
