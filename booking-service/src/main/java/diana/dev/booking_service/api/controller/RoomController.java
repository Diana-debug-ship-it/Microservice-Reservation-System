package diana.dev.booking_service.api.controller;


import diana.dev.booking_service.api.dto.room.RoomRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.domain.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDto> create(
            @PathVariable("hotelId") Long hotelId,
            @Valid @RequestBody RoomRequestDto request
    ) {
        log.info("Creating room {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(hotelId, request));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> getById(
            @PathVariable("hotelId") Long hotelId,
            @PathVariable("roomId") Long roomId
    ) {
        log.info("Retrieving room by id {} in hotel with id={}", roomId, hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getRoomById(hotelId, roomId));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllByHotelId(
            @PathVariable("hotelId") Long hotelId
    ) {
        log.info("Retrieving all rooms in hotel with id={}", hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAllRooms(hotelId));
    }

}
