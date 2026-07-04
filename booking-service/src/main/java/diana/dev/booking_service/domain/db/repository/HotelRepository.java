package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
}
