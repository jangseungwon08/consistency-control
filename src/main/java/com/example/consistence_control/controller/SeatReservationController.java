package com.example.consistence_control.controller;

import com.example.consistence_control.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/{performanceId}/seat", produces = MediaType.APPLICATION_JSON_VALUE)
public class SeatReservationController {

    private final SeatService seatService;

    @PostMapping(value = "/{id}/hold")
    public ResponseEntity<Void> holdSeat(
            @PathVariable Long performanceId, @PathVariable Long id,
            @RequestParam String userId
    ){
        seatService.holdSeat(userId, id, performanceId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}/reserve")
    public ResponseEntity<Void> reserveSeat(
            @PathVariable Long performanceId, @PathVariable Long id,
            @RequestParam String userId
    ){
        seatService.reserveSeat(userId, id, performanceId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> releaseSeat(
            @PathVariable Long performanceId, @PathVariable Long id,
            @RequestParam String userId
    ){
        seatService.releaseSeat(userId, id, performanceId);
        return ResponseEntity.ok().build();
    }
}
