package diana.dev.booking_service.domain.db;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "room_seq"
    )
    @SequenceGenerator(
            name = "room_seq",
            sequenceName = "rooms_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "hotel_number", nullable = false)
    private String number;

    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Column(name = "price_per_night", nullable = false)
    private Integer maxGuests;
}
