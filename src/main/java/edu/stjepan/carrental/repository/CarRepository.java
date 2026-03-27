package edu.stjepan.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.stjepan.carrental.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
	boolean existsByRegistrationNumber(String registrationNumber);
}
