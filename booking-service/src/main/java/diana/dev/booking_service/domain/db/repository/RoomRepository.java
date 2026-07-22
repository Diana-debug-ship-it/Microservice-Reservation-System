package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.api.dto.BookingDetailsSnapshot;
import diana.dev.booking_service.domain.db.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByHotelId(Long hotelId);

    Optional<RoomEntity> findByIdAndHotelId(Long roomId, Long hotelId);


    @Query("""
            SELECT new diana.dev.booking_service.api.dto.BookingDetailsSnapshot(h.name, r.roomNumber, r.pricePerNight
            FROM RoomEntity r
            JOIN r.hotel h
            WHERE r.id = :roomId AND h.id = :hotelId
            """)
    Optional<BookingDetailsSnapshot> getSnapshotForBooking(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId
    );
}
