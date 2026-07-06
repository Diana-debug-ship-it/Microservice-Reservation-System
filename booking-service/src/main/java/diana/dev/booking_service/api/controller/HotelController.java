package diana.dev.booking_service.api.controller;

import diana.dev.booking_service.api.dto.hotel.HotelRequestDto;
import diana.dev.booking_service.api.dto.hotel.HotelResponseDto;
import diana.dev.booking_service.domain.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    public ResponseEntity<HotelResponseDto> create(
            @Valid @RequestBody HotelRequestDto request
    ) {
        log.info("Creating hotel {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(request));
    }

    public ResponseEntity<HotelResponseDto> getById(
            @PathVariable("id") Long id
    ) {
        log.info("Retrieving hotel by id={}", id);
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getHotelById(id));
    }

    public ResponseEntity<List<HotelResponseDto>> getAll() {
        log.info("Retrieving all hotels");
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getAllHotels());
    }

}
