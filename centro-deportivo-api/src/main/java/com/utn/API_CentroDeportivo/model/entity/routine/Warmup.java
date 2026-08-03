package com.utn.API_CentroDeportivo.model.entity.routine;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class Warmup {
    @Column(name = "warmup_duration_minutes")
    private Integer durationMinutes;

    @ElementCollection
    @CollectionTable(name = "routine_warmup_activities",
            joinColumns = @JoinColumn(name = "routine_id"))
    @Column(name = "activity")
    private List<String> activities = new ArrayList<>();
}