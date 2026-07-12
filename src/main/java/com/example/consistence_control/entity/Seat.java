package com.example.consistence_control.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private String userId;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Column(name = "seat_premption_time")
    private LocalDateTime preemptionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Performance performance;

    public void hold(String userId, LocalDateTime heldAt){
        if(this.seatStatus != SeatStatus.AVAILABLE){
            throw new IllegalStateException("이미 선점된 좌석입니다.");
        }

        this.userId = userId;
        this.preemptionTime = heldAt;
        this.seatStatus = SeatStatus.HELD;
    }

    public void reserve(String userId){
        if(this.seatStatus != SeatStatus.HELD){
            throw new IllegalStateException("선점 상태가 아닙니다.");
        }
        if(!userId.equals(this.userId)) {
            throw new IllegalStateException("사용자가 다릅니다.");
        }

        this.seatStatus = SeatStatus.RESERVED;
    }

    public void release(){
        this.seatStatus = SeatStatus.AVAILABLE;
        this.userId = null;
        this.preemptionTime = null;
    }

}
