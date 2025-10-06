package example.CptFranck.BookingService.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventBis {

    private Long userId;

    private Long eventId;

    private Long ticketCount;

    private BigDecimal totalPrice;
}
