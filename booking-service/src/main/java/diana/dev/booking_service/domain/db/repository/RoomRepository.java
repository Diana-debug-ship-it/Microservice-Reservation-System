package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}
