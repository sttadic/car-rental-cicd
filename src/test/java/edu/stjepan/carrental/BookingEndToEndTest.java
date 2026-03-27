package edu.stjepan.carrental;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import edu.stjepan.carrental.dto.BookingDTO;
import edu.stjepan.carrental.dto.CarDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingEndToEndTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void fullBookingLifecycle_createCarThenBookThenRetrieve() {
    // 1. Create a new car via the real API
    String carJson = """
        {
          "make": "BMW",
          "model": "X5",
          "registrationNumber": "E2E-001",
          "year": 2024,
          "dailyRate": 80.00
        }
        """;

    ResponseEntity<CarDTO> carResponse = restTemplate.postForEntity(
        "/api/cars", jsonEntity(carJson), CarDTO.class);

    assertThat(carResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    CarDTO createdCar = carResponse.getBody();
    assertThat(createdCar).isNotNull();
    assertThat(createdCar.getMake()).isEqualTo("BMW");
    assertThat(createdCar.getId()).isNotNull();

    // 2. Create a booking for that car
    String bookingJson = """
        {
          "carId": %d,
          "customerName": "Jane Doe",
          "customerEmail": "jane@example.com",
          "startDate": "2025-09-01",
          "endDate": "2025-09-04",
          "status": "CREATED"
        }
        """.formatted(createdCar.getId());

    ResponseEntity<BookingDTO> bookingResponse = restTemplate.postForEntity(
        "/api/bookings", jsonEntity(bookingJson), BookingDTO.class);

    assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    BookingDTO createdBooking = bookingResponse.getBody();
    assertThat(createdBooking).isNotNull();
    assertThat(createdBooking.getCustomerName()).isEqualTo("Jane Doe");
    assertThat(createdBooking.getCarId()).isEqualTo(createdCar.getId());
    assertThat(createdBooking.getTotalAmount())
        .isEqualByComparingTo(new BigDecimal("240.00")); // 3 days * 80.00

    // 3. Retrieve the booking by ID and verify it was persisted
    ResponseEntity<BookingDTO> getResponse = restTemplate.getForEntity(
        "/api/bookings/" + createdBooking.getId(), BookingDTO.class);

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    BookingDTO fetchedBooking = getResponse.getBody();
    assertThat(fetchedBooking).isNotNull();
    assertThat(fetchedBooking.getId()).isEqualTo(createdBooking.getId());
    assertThat(fetchedBooking.getCustomerEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void getBooking_whenDoesNotExist_shouldReturn404() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/bookings/99999", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void createBooking_whenCarDoesNotExist_shouldReturn404() {
    String bookingJson = """
        {
          "carId": 99999,
          "customerName": "Nobody",
          "customerEmail": "nobody@example.com",
          "startDate": "2025-09-01",
          "endDate": "2025-09-04",
          "status": "CREATED"
        }
        """;

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/bookings", jsonEntity(bookingJson), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private org.springframework.http.HttpEntity<String> jsonEntity(String json) {
    var headers = new org.springframework.http.HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    return new org.springframework.http.HttpEntity<>(json, headers);
  }
}
