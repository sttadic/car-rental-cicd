package edu.stjepan.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.stjepan.carrental.dto.*;
import edu.stjepan.carrental.service.CarService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/cars")
public class CarController {
	private final CarService carService;

	@Autowired
	public CarController(CarService carService) {
		this.carService = carService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CarDTO createCar(@Valid @RequestBody CreateCarRequest request) {
		return carService.createCar(request);
	}

	@GetMapping("/{id}")
	public CarDTO getCar(@PathVariable Long id) {
		return carService.getCarById(id);
	}

	@GetMapping
	public Page<CarDTO> getCars(Pageable pageable) {
		return carService.getAllCars(pageable);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCar(@PathVariable Long id) {
		carService.deleteCar(id);
	}
}
