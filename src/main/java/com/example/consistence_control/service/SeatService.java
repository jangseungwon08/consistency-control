package com.example.consistence_control.service;

import com.example.consistence_control.entity.Seat;
import com.example.consistence_control.entity.SeatStatus;
import com.example.consistence_control.repository.PerformanceRepository;
import com.example.consistence_control.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;

    @Transactional
    public void holdSeat(String userId, Long seatId, Long performanceId){
//        해당 공연에 대해 좌석이 있는지 확인
        Seat findSeat = seatRepository.findByIdWithLock(seatId, performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
        //        Hold인데 점유 시간이 지났을 때
        if(findSeat.getSeatStatus() == SeatStatus.HELD && LocalDateTime.now().minusMinutes(5L).isAfter(findSeat.getPreemptionTime())){
            findSeat.release();
        }
        findSeat.hold(userId, LocalDateTime.now());
    }
    @Transactional
    public void reserveSeat(String userId, Long seatId, Long performanceId){
        Seat findSeat = seatRepository.findByIdWithLock(seatId, performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
        //        Hold인데 점유 시간이 지났을 때
        if(findSeat.getSeatStatus() == SeatStatus.HELD && LocalDateTime.now().minusMinutes(5L).isAfter(findSeat.getPreemptionTime())){
            findSeat.release();
            throw new IllegalStateException("5분 선점이 만료되었습니다.");
        }
        findSeat.reserve(userId);
    }

    @Transactional
    public void releaseSeat(String userId, Long seatId, Long performanceId){
//        seatStatus한 시간이 지나거나 환불 했을 때 분기를 두번 나눠야됨
        Seat findSeat = seatRepository.findByIdWithLock(seatId, performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
        if(!findSeat.getUserId().equals(userId)) throw new IllegalStateException("유저 아이디가 다릅니다.");
        findSeat.release();
    }
}
