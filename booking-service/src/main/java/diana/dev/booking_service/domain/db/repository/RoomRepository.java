package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByHotelId(Long hotelId);

    Optional<RoomEntity> findByIdAndHotelId(Long roomId, Long hotelId);
}
