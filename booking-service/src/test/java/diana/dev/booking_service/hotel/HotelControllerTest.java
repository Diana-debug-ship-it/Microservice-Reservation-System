package diana.dev.booking_service.hotel;


import diana.dev.booking_service.api.controller.HotelController;
import diana.dev.booking_service.api.dto.hotel.HotelRequestDto;
import diana.dev.booking_service.api.dto.hotel.HotelResponseDto;
import diana.dev.booking_service.domain.HotelService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(HotelController.class)
public class HotelControllerTest {

    private static final String BASE_URL = "/api/v1/hotels";

    @MockitoBean
    private HotelService hotelService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getById_ShouldReturnHotelResponseDto_WhenHotelExists() throws Exception {

        Long id = 10L;
        HotelResponseDto mockHotel = new HotelResponseDto(id, "hotel", "address", new ArrayList<>());

        when(hotelService.getHotelById(id)).thenReturn(mockHotel);

        mockMvc.perform(get(BASE_URL+"/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("hotel"))
                .andExpect(jsonPath("$.address").value("address"));

        verify(hotelService, times(1)).getHotelById(id);

    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenHotelNotExists() throws Exception {

        Long id = 10L;

        when(hotelService.getHotelById(id)).thenThrow(new EntityNotFoundException("Hotel not found"));

        mockMvc.perform(get(BASE_URL+"/{id}", id))
                .andExpect(status().isNotFound());

    }

    @Test
    void getAllHotels_ShouldReturnListOfHotels_WhenNoHotelExists() throws Exception {

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    void getAllHotels_ShouldReturnListOfHotels_WhenHotelsExist() throws Exception {

        HotelResponseDto mockHotel1 = new HotelResponseDto(1L, "hotel1", "address1", new ArrayList<>());
        HotelResponseDto mockHotel2 = new HotelResponseDto(2L, "hotel2", "address2", new ArrayList<>());
        HotelResponseDto mockHotel3 = new HotelResponseDto(3L, "hotel3", "address3", new ArrayList<>());

        when(hotelService.getAllHotels()).thenReturn(Arrays.asList(mockHotel1, mockHotel2, mockHotel3));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));

    }

    @Test
    void createHotel_ShouldReturnHotelWithGeneratedId_WhenHotelIsValid() throws Exception {

        Long id = 5L;
        HotelRequestDto hotelToSave = new HotelRequestDto("hotel", "address");
        HotelResponseDto savedHotel = new HotelResponseDto(id, "hotel", "address", new ArrayList<>());

        when((hotelService.createHotel(hotelToSave))).thenReturn(savedHotel);
        String hotelJson = objectMapper.writeValueAsString(hotelToSave);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createHotel_ShouldReturnBadRequest_WhenHotelAlreadyExists() throws Exception {

        HotelRequestDto hotelToSave = new HotelRequestDto("hotel", "address");

        when(hotelService.createHotel(hotelToSave)).thenThrow(new IllegalArgumentException("A hotel with this name already exists in this city"));

        String hotelJson = objectMapper.writeValueAsString(hotelToSave);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHotel_ShouldReturnBadRequest_WhenHotelNameIsEmptyOrNull() throws Exception {

        HotelRequestDto emptyName = new HotelRequestDto("", "address");
        String hotelJson = objectMapper.writeValueAsString(emptyName);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson))
                .andExpect(status().isBadRequest());

        HotelRequestDto nullName = new HotelRequestDto(null, "address");
        String nullJson = objectMapper.writeValueAsString(nullName);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHotel_ShouldReturnBadRequest_WhenHotelAddressIsEmptyOrNull() throws Exception {

        HotelRequestDto emptyAddress = new HotelRequestDto("hotel", "");
        String hotelJson = objectMapper.writeValueAsString(emptyAddress);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson))
                .andExpect(status().isBadRequest());

        HotelRequestDto nullAddress = new HotelRequestDto("hotel", null);
        String nullJson = objectMapper.writeValueAsString(nullAddress);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());
    }
}
