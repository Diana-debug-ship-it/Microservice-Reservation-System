package diana.dev.booking_service.domain.db.repository;

import diana.dev.booking_service.domain.db.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query(
            """
                    SELECT COUNT(b) > 0 BookingEntity b
                    WHERE b.roomId = :roomId
                    AND b.status != 'CANCELLED'
                    AND b.checkInDate < :checkInDate
                    AND b.checkOutDate > :checkOutDate
                    """
    )
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

}
