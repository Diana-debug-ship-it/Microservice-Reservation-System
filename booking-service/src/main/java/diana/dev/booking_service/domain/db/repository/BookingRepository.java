package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query(
            """
                    SELECT COUNT(b) > 0
                    FROM BookingEntity b
                    WHERE b.roomId = :roomId
                    AND b.status != 'CANCELLED'
                    AND b.checkInDate < :checkOutDate
                    AND b.checkOutDate > :checkInDate
                    """
    )
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query(
            """
                    SELECT b FROM BookingEntity b
                    JOIN RoomEntity r ON b.roomId = r.id
                    WHERE r.hotel.id = :hotelId
                    """
    )
    List<BookingEntity> findAllByHotelId(
            @Param("hotelId") Long hotelId
    );

    @Query(
            """
                    SELECT b FROM BookingEntity b
                    JOIN RoomEntity r ON b.roomId = r.id
                    WHERE b.id = :bookingId AND r.hotel.id = :hotelId
                    """
    )
    Optional<BookingEntity> findByIdAndHotelId(
            @Param("bookingId") Long bookingId,
            @Param("hotelId") Long hotelId
    );


}
