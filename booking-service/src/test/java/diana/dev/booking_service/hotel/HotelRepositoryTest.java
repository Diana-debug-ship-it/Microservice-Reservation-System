package diana.dev.booking_service.hotel;

import diana.dev.booking_service.domain.db.entity.HotelEntity;
import diana.dev.booking_service.domain.db.repository.HotelRepository;
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

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class HotelRepositoryTest {

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
    private HotelRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByNameAndAddress_ShouldReturnFalse_WhenHotelIsUnique() {

        HotelEntity hotelToSave = new HotelEntity(null, "hotel", "address", List.of());

        Assertions.assertFalse(repository.existsByNameAndAddress(hotelToSave.getName(), hotelToSave.getAddress()));

    }


    @Test
    void existsByNameAndAddress_ShouldReturnTrue_WhenHotelAlreadyExists() {

        HotelEntity hotelToSave = new HotelEntity(null, "hotel", "address", List.of());
        repository.save(hotelToSave);

        Assertions.assertTrue(repository.existsByNameAndAddress(hotelToSave.getName(), hotelToSave.getAddress()));

    }

}
