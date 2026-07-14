package diana.dev.booking_service.domain.db.entity;


import diana.dev.shared.http.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_seq"
    )
    @SequenceGenerator(
            name = "booking_seq",
            sequenceName = "booking_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "guests_count", nullable = false)
    private Integer guestsCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "booking_preferences")
    private String bookingPreferences;
}
