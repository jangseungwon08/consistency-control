package com.example.consistence_control;

import com.example.consistence_control.entity.Performance;
import com.example.consistence_control.entity.Seat;
import com.example.consistence_control.entity.SeatStatus;
import com.example.consistence_control.repository.PerformanceRepository;
import com.example.consistence_control.repository.SeatRepository;
import com.example.consistence_control.service.SeatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class SeatServiceIntegrationTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Long performanceId;
    private Long seatId;

    @BeforeEach
    void setup(){
        Performance performance = Performance.builder()
                .name("오페라의 유령")
                .createdAt(LocalDateTime.now())
                .totalSeatCount(1)
                .build();

        Seat seat = Seat.builder()
                .seatStatus(SeatStatus.AVAILABLE)
                .performance(performance)
                .build();
        performanceRepository.save(performance);
        seatRepository.save(seat);

        performanceId = performance.getId();
        seatId = seat.getId();
    }

    @AfterEach
    void clear(){
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
    }

    @Test
    @DisplayName(value = "500명 중 1명만 좌석 선점에 성공할 수 있다.")
    public void preemptionTest() throws InterruptedException {
//        given
//        스레드 500개 생성
        ExecutorService executorService = Executors.newFixedThreadPool(500);
//        스레드 500개 준비됐을 때 까지 준비
        CountDownLatch latch = new CountDownLatch(500);
        CountDownLatch doneLatch = new CountDownLatch(500);    // 전부 끝날 때까지
        AtomicInteger successCount = new AtomicInteger(0);

//        when
        for(int i = 0; i< 500; i++){
            int userNum = i;
            executorService.submit(() -> {
                try{
                    latch.countDown();
                    latch.await();
                    seatService.holdSeat("user" + userNum, seatId, performanceId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally{
                    doneLatch.countDown();
                }
            });
        }
        doneLatch.await();

//        then
        assertThat(successCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "선점 후 5분이 지나고 다른 사람이 선점 할 수 있다.")
    public void anotherPreemptionTest(){
//        given
        String user1 = "user1";
        String user2 = "user2";

//        when
        seatService.holdSeat(user1, seatId, performanceId);
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("레포지토리 오류"));
        seat.setPreemptionTime(LocalDateTime.now().minusMinutes(6L));
        seatRepository.save(seat);


        seatService.holdSeat(user2, seatId, performanceId);
        Seat seat2 = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("레포지토리 오류"));

//        then
        assertThat(seat2.getUserId()).isEqualTo(user2);
    }

    @Test
    @DisplayName(value = "이미 선점된 좌석에 다른 사람이 선점하면 예외가 터진다.")
    public void duplicationPreemptionSeatTest() {
//        given
        String user1 = "user1";
        String user2 = "user2";

        seatService.holdSeat(user1, seatId, performanceId);
        assertThatThrownBy(() -> seatService.holdSeat(user2, seatId, performanceId))
                .isInstanceOf(IllegalStateException.class);
    }
}
