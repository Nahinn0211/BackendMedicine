package hunre.edu.vn.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppointmentStatus {
    SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
}
