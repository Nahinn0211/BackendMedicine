package hunre.edu.vn.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}
