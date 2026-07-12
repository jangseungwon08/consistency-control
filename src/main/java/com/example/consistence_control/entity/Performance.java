package com.example.consistence_control.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200)
    private String name;

    @Column
    private LocalDateTime createdAt;

    @Column
    private Integer totalSeatCount;

    @OneToMany(mappedBy = "performance")
    private List<Seat> seats;
}
