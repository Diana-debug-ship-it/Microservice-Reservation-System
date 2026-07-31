package diana.dev.booking_service.booking;


import diana.dev.booking_service.api.controller.BookingController;
import diana.dev.booking_service.api.dto.BookingPaymentRequest;
import diana.dev.booking_service.api.dto.booking.BookingDto;
import diana.dev.booking_service.api.dto.booking.CreateBookingRequestDto;
import diana.dev.booking_service.domain.BookingProcessor;
import diana.dev.shared.http.booking.BookingStatus;
import diana.dev.shared.http.payment.CardDetailsDto;
import diana.dev.shared.http.payment.PaymentMethod;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    private static final String BASE_URL = "/api/v1/hotels";

    @MockitoBean
    private BookingProcessor bookingProcessor;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getById_ShouldReturnBookingDto_WhenBookingExists() throws Exception {

        Long bookingId = 5L;
        Long hotelId = 2L;

        String url = BASE_URL + "/{hotelId}/booking";

        BookingDto bookingDto = createMockBooking(bookingId, hotelId);
        when(bookingProcessor.getBookingById(hotelId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get(url+"/{bookingId}", hotelId, bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.hotelId").value(hotelId));
    }

    @Test
    void getById_ShouldThrowEntityNotFound_WhenBookingDoesNotExist() throws Exception {

        String url = BASE_URL + "/{hotelId}/booking";

        when(bookingProcessor.getBookingById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException("Booking not found"));

        mockMvc.perform(get(url+"/{bookingId}", 2L, 4L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnListOfBookings_WhenBookingsExist() throws Exception {

        Long hotelId = 2L;
        BookingDto booking1 = createMockBooking(1L, hotelId);
        BookingDto booking2 = createMockBooking(2L, hotelId);
        BookingDto booking3 = createMockBooking(3L, hotelId);

        when(bookingProcessor.getAllBookings(hotelId)).thenReturn(List.of(booking1, booking2, booking3));

        String url = BASE_URL + "/{hotelId}/booking";
        mockMvc.perform(get(url, hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));

    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoBookingExists() throws Exception {

        Long hotelId = 2L;

        when(bookingProcessor.getAllBookings(hotelId)).thenReturn(List.of());

        String url = BASE_URL + "/{hotelId}/booking";
        mockMvc.perform(get(url, hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    void create_ShouldReturnBookingDto_WhenBookingIsValid() throws Exception {

        Long hotelId = 2L;
        Long bookingId = 5L;

        CreateBookingRequestDto request = new CreateBookingRequestDto(
                2L,
                2,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");

        BookingDto bookingDto = new BookingDto(
                bookingId,
                request.roomId(),
                "101",
                hotelId,
                "hotel",
                request.guests(),
                request.checkInDate(),
                request.checkOutDate(),
                BookingStatus.PENDING_PAYMENT,
                BigDecimal.valueOf(3000),
                request.bookingPreferences()
        );
        String bookingJson = objectMapper.writeValueAsString(request);

        when(bookingProcessor.createBooking(hotelId, request)).thenReturn(bookingDto);

        String url = BASE_URL + "/{hotelId}/booking";
        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));

    }

    @Test
    void create_ShouldReturnBadRequest_WhenRoomIsNull() throws Exception {

        Long hotelId = 2L;

        CreateBookingRequestDto request = new CreateBookingRequestDto(
                null,
                2,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");

        String bookingJson = objectMapper.writeValueAsString(request);

        String url = BASE_URL + "/{hotelId}/booking";
        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void create_ShouldReturnBadRequest_WhenGuestsIsNullOrNotPositive() throws Exception {

        Long hotelId = 2L;
        String url = BASE_URL + "/{hotelId}/booking";

        CreateBookingRequestDto requestNullGuests = new CreateBookingRequestDto(
                2L,
                null,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");
        String nullGuestsJson = objectMapper.writeValueAsString(requestNullGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullGuestsJson))
                .andExpect(status().isBadRequest());

        CreateBookingRequestDto requestZeroGuests = new CreateBookingRequestDto(
                2L,
                0,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");
        String zeroGuestsJson = objectMapper.writeValueAsString(requestZeroGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(zeroGuestsJson))
                .andExpect(status().isBadRequest());

        CreateBookingRequestDto requestNegativeGuests = new CreateBookingRequestDto(
                2L,
                -4,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");
        String negativeGuestsJson = objectMapper.writeValueAsString(requestNegativeGuests);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(negativeGuestsJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void create_ShouldReturnBadRequest_WhenCheckInDateIsNotValid() throws Exception {

        Long hotelId = 2L;
        String url = BASE_URL + "/{hotelId}/booking";

        CreateBookingRequestDto requestNullCheckInDate = new CreateBookingRequestDto(
                2L,
                2,
                null,
                LocalDate.now().plusDays(10),
                "big windows");
        String nullCheckInDateJson = objectMapper.writeValueAsString(requestNullCheckInDate);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullCheckInDateJson))
                .andExpect(status().isBadRequest());


        CreateBookingRequestDto requestPastCheckInDate = new CreateBookingRequestDto(
                2L,
                2,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(10),
                "big windows");
        String pastCheckInDateJson = objectMapper.writeValueAsString(requestPastCheckInDate);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pastCheckInDateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturnBadRequest_WhenCheckOutDateIsNotValid() throws Exception {

        Long hotelId = 2L;
        String url = BASE_URL + "/{hotelId}/booking";

        CreateBookingRequestDto requestNullCheckOutDate = new CreateBookingRequestDto(
                2L,
                2,
                LocalDate.now().plusDays(1),
                null,
                "big windows");
        String nullCheckOutDateJson = objectMapper.writeValueAsString(requestNullCheckOutDate);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullCheckOutDateJson))
                .andExpect(status().isBadRequest());


        CreateBookingRequestDto requestPastCheckOutDate = new CreateBookingRequestDto(
                2L,
                2,
                LocalDate.now().plusDays(1),
                LocalDate.now().minusDays(10),
                "big windows");
        String pastCheckOutDateJson = objectMapper.writeValueAsString(requestPastCheckOutDate);

        mockMvc.perform(post(url, hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pastCheckOutDateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void payBooking_ShouldReturnConfirmedBooking_WhenPaymentSucceeded() throws Exception {

        Long hotelId = 2L;
        Long bookingId = 5L;
        String url = BASE_URL + "/{hotelId}/booking/{bookingId}/pay";

        BookingPaymentRequest paymentRequest = new BookingPaymentRequest(PaymentMethod.CARD, new CardDetailsDto("1234567812345678", "123"));

        BookingDto paidBooking = new BookingDto(
                bookingId,
                3L,
                "101",
                hotelId,
                "hotel",
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(4),
                BookingStatus.CONFIRMED,
                BigDecimal.valueOf(1000),
                "preferences"
        );

        when(bookingProcessor.processPayment(hotelId, bookingId, paymentRequest)).thenReturn(paidBooking);

        String requestJson = objectMapper.writeValueAsString(paymentRequest);

        mockMvc.perform(post(url, hotelId, bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

    }

    @Test
    void payBooking_ShouldReturnBadRequest_WhenPaymentMethodIsNotValid() throws Exception {

        Long hotelId = 2L;
        Long bookingId = 5L;
        String url = BASE_URL + "/{hotelId}/booking/{bookingId}/pay";

        BookingPaymentRequest paymentMethodNull = new BookingPaymentRequest(null, new CardDetailsDto("1234567812345678", "123"));
        String paymentMethodNullJson = objectMapper.writeValueAsString(paymentMethodNull);

        mockMvc.perform(post(url, hotelId, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentMethodNullJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void payBooking_ShouldReturnBadRequest_WhenPaymentDetailsIsNotValid() throws Exception {

        Long hotelId = 2L;
        Long bookingId = 5L;
        String url = BASE_URL + "/{hotelId}/booking/{bookingId}/pay";

        BookingPaymentRequest paymentDetailsNull = new BookingPaymentRequest(PaymentMethod.CARD, null);
        String paymentDetailsNullJson = objectMapper.writeValueAsString(paymentDetailsNull);

        mockMvc.perform(post(url, hotelId, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentDetailsNullJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void payBooking_ShouldReturnBadRequest_WhenIllegalStateException() throws Exception {

        Long hotelId = 2L;
        Long bookingId = 5L;
        String url = BASE_URL + "/{hotelId}/booking/{bookingId}/pay";

        BookingPaymentRequest paymentRequest = new BookingPaymentRequest(PaymentMethod.CARD, new CardDetailsDto("1234567812345678", "123"));
        String requestJson = objectMapper.writeValueAsString(paymentRequest);

        when(bookingProcessor.processPayment(hotelId, bookingId, paymentRequest)).thenThrow(new IllegalStateException("Booking must be in status PENDING_PAYMENT"));

        mockMvc.perform(post(url, hotelId, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

    }

    private BookingDto createMockBooking(Long bookingId, Long hotelId) {
        return new BookingDto(
                bookingId,
                3L,
                "101",
                hotelId,
                "hotel",
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(4),
                BookingStatus.CONFIRMED,
                BigDecimal.valueOf(1000),
                "preferences"
        );
    }
}
