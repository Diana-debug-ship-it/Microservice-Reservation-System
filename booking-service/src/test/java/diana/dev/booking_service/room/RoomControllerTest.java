package diana.dev.booking_service.room;

import diana.dev.booking_service.api.controller.RoomController;
import diana.dev.booking_service.api.dto.room.RoomRequestDto;
import diana.dev.booking_service.api.dto.room.RoomResponseDto;
import diana.dev.booking_service.domain.RoomService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    private static final String BASE_URL = "/api/v1/hotels";

    @MockitoBean
    private RoomService roomService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnRoomResponse_WhenRoomIsValid() throws Exception {

        Long id = 5L;
        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";
        RoomRequestDto requestDto = new RoomRequestDto("101", BigDecimal.valueOf(1000), 2);
        RoomResponseDto responseDto = new RoomResponseDto(id, hotelId, "101", BigDecimal.valueOf(1000), 2);

        when(roomService.createRoom(hotelId, requestDto)).thenReturn(responseDto);
        String roomJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(url, hotelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRoomNumberIsEmptyOrNull() throws Exception {

        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";
        RoomRequestDto emptyNumber = new RoomRequestDto("", BigDecimal.valueOf(1000), 2);
        String roomJson = objectMapper.writeValueAsString(emptyNumber);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomJson))
                .andExpect(status().isBadRequest());

        RoomRequestDto nullNumber = new RoomRequestDto(null, BigDecimal.valueOf(1000), 2);
        String nullJson = objectMapper.writeValueAsString(nullNumber);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturnBadRequest_WhenPricePerNightIsNullOrNotPositive() throws Exception {

        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";

        RoomRequestDto nullPrice = new RoomRequestDto("101", null, 2);
        String nullJson = objectMapper.writeValueAsString(nullPrice);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());


        RoomRequestDto zeroPrice = new RoomRequestDto("101", BigDecimal.valueOf(0), 2);
        String zeroJson = objectMapper.writeValueAsString(zeroPrice);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(zeroJson))
                .andExpect(status().isBadRequest());

        RoomRequestDto negativePrice = new RoomRequestDto("101", BigDecimal.valueOf(0), 2);
        String negativeJson = objectMapper.writeValueAsString(negativePrice);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(negativeJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMaxGuestsIsNullOrNotPositive() throws Exception {

        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";

        RoomRequestDto nullGuests = new RoomRequestDto("101", BigDecimal.valueOf(1000), null);
        String nullJson = objectMapper.writeValueAsString(nullGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());


        RoomRequestDto zeroGuests = new RoomRequestDto("101", BigDecimal.valueOf(1000), 0);
        String zeroJson = objectMapper.writeValueAsString(zeroGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(zeroJson))
                .andExpect(status().isBadRequest());

        RoomRequestDto negativeGuests = new RoomRequestDto("101", BigDecimal.valueOf(1000), -3);
        String negativeJson = objectMapper.writeValueAsString(negativeGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(negativeJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnRoomResponse_WhenRoomExists() throws Exception {

        Long id = 5L;
        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms/{id}";

        RoomResponseDto responseDto = new RoomResponseDto(id, hotelId, "101", BigDecimal.valueOf(1000), 2);

        when(roomService.getRoomById(hotelId, id)).thenReturn(responseDto);

        mockMvc.perform(get(url, hotelId, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.hotelId").value(3))
                .andExpect(jsonPath("$.number").value("101"))
                .andExpect(jsonPath("$.pricePerNight").value(1000))
                .andExpect(jsonPath("$.maxGuests").value(2));
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenRoomDoesNotExist() throws Exception {

        Long id = 5L;
        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms/{id}";

        when(roomService.getRoomById(hotelId, id)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(get(url, hotelId, id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRooms_ShouldReturnListOfRooms_WhenRoomsExist() throws Exception {

        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";

        RoomResponseDto response1 = new RoomResponseDto(1L, hotelId, "101", BigDecimal.valueOf(1000), 2);
        RoomResponseDto response2 = new RoomResponseDto(2L, hotelId, "102", BigDecimal.valueOf(2000), 3);
        RoomResponseDto response3 = new RoomResponseDto(3L, hotelId, "103", BigDecimal.valueOf(1500), 2);

        when(roomService.getAllRooms(hotelId)).thenReturn(List.of(response1, response2, response3));

        mockMvc.perform(get(url, hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));
    }

    @Test
    void getAllRooms_ShouldReturnEmptyList_WhenNoRoomsExist() throws Exception {

        Long hotelId = 3L;
        String url = BASE_URL + "/{hotelId}/rooms";

        when(roomService.getAllRooms(hotelId)).thenReturn(List.of());

        mockMvc.perform(get(url, hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
