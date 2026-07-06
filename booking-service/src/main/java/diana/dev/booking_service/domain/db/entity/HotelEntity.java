package diana.dev.booking_service.domain.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "hotels")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HotelEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "hotel_seq"
    )
    @SequenceGenerator(
            name = "hotel_seq",
            sequenceName = "hotels_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "hotel_name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomEntity> rooms;

}
