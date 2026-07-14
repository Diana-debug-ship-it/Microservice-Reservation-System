package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.entity.HotelEntity;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    boolean existsByNameAndAddress(@NotNull String name, @NotNull String address);

}
