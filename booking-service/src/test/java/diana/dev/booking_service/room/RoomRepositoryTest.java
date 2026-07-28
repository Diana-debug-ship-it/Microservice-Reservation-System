package diana.dev.booking_service.room;

import diana.dev.booking_service.api.dto.BookingDetailsSnapshot;
import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.entity.RoomEntity;
import diana.dev.booking_service.domain.db.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class RoomRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RoomRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByHotelId_ShouldReturnListOfRooms_WhenRoomsExist() {

        HotelEntity hotelA = new HotelEntity(null, "Hotel A", "Address A", List.of());
        HotelEntity hotelB = new HotelEntity(null, "Hotel B", "Address B", List.of());

        entityManager.persistAndFlush(hotelA);
        entityManager.persistAndFlush(hotelB);

        RoomEntity room1HotelA = new RoomEntity(null, "101", BigDecimal.valueOf(1000), hotelA, 2);
        RoomEntity room2HotelA = new RoomEntity(null, "102", BigDecimal.valueOf(1500), hotelA, 3);
        RoomEntity roomHotelB = new RoomEntity(null, "999", BigDecimal.valueOf(5000), hotelB, 4);

        entityManager.persist(room1HotelA);
        entityManager.persist(room2HotelA);
        entityManager.persist(roomHotelB);

        entityManager.flush();
        entityManager.clear();

        List<RoomEntity> rooms = repository.findByHotelId(hotelA.getId());

        Assertions.assertEquals(2, rooms.size());
        Assertions.assertTrue(rooms.stream().anyMatch(r -> r.getNumber().equals("101")));
        Assertions.assertTrue(rooms.stream().anyMatch(r -> r.getNumber().equals("102")));

        Assertions.assertTrue(rooms.stream().noneMatch(r -> r.getNumber().equals("999")));

    }

    @Test
    void findByHotelId_ShouldReturnEmptyList_WhenNoRoomsExistInHotel() {

        HotelEntity hotelA = new HotelEntity(null, "Hotel A", "Address A", List.of());
        HotelEntity hotelB = new HotelEntity(null, "Hotel B", "Address B", List.of());

        entityManager.persistAndFlush(hotelA);
        entityManager.persistAndFlush(hotelB);

        RoomEntity roomHotelB = new RoomEntity(null, "999", BigDecimal.valueOf(5000), hotelB, 4);

        entityManager.persistAndFlush(roomHotelB);

        entityManager.clear();

        List<RoomEntity> rooms = repository.findByHotelId(hotelA.getId());

        Assertions.assertTrue(rooms.isEmpty());

    }

    @Test
    void findByIdAndHotelId_ShouldReturnRoomEntity_WhenRoomExists() {

        HotelEntity hotel = new HotelEntity(null, "Hotel", "Address", List.of());
        RoomEntity roomHotel = new RoomEntity(null, "999", BigDecimal.valueOf(5000), hotel, 4);

        entityManager.persist(hotel);
        entityManager.persist(roomHotel);

        entityManager.flush();
        entityManager.clear();

        Optional<RoomEntity> found = repository.findByIdAndHotelId(roomHotel.getId(), hotel.getId());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(roomHotel.getId(), found.get().getId());

    }

    @Test
    void findByIdAndHotelId_ShouldReturnEmptyOptional_WhenRoomDoesNotExist() {

        HotelEntity hotel = new HotelEntity(null, "Hotel", "Address", List.of());

        entityManager.persist(hotel);

        entityManager.flush();
        entityManager.clear();

        Optional<RoomEntity> found = repository.findByIdAndHotelId(1L, hotel.getId());

        Assertions.assertTrue(found.isEmpty());

    }

    @Test
    void getSnapshotForBooking_ShouldReturnSnapshot_WhenRoomInHotelExists() {

        HotelEntity hotel = new HotelEntity(null, "Hotel", "Address", List.of());
        RoomEntity roomHotel = new RoomEntity(null, "999", BigDecimal.valueOf(5000), hotel, 4);

        entityManager.persist(hotel);
        entityManager.persist(roomHotel);

        entityManager.flush();
        entityManager.clear();

        Optional<BookingDetailsSnapshot> snapshot = repository.getSnapshotForBooking(hotel.getId(), roomHotel.getId());

        Assertions.assertTrue(snapshot.isPresent());
        Assertions.assertEquals(hotel.getName(), snapshot.get().hotelName());
        Assertions.assertEquals(roomHotel.getNumber(), snapshot.get().roomNumber());
        Assertions.assertEquals(0, roomHotel.getPricePerNight().compareTo(snapshot.get().pricePerNight()));

    }

    @Test
    void getSnapshotForBooking_ShouldReturnEmptyOptional_WhenRoomDoesNotExist() {


        Optional<BookingDetailsSnapshot> snapshot = repository.getSnapshotForBooking(1L, 4L);

        Assertions.assertTrue(snapshot.isEmpty());

    }

}
