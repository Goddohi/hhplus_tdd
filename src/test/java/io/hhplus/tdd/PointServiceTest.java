package io.hhplus.tdd;


import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    PointService pointService;

    @Mock
    UserPointRepository userPointRepository;
    @Mock
    PointHistoryRepository pointHistoryRepository;


    @Test
    @DisplayName("유저 ID로 포인트를 조회한다")
    void SelectUserId_ReturnPoint() {
        long userId = 1L;
        UserPoint expected = new UserPoint(userId, 10000L, Instant.parse("2025-08-15T00:00:00Z").toEpochMilli());
        given(userPointRepository.selectById(userId)).willReturn(expected);

        // when
        UserPoint result = pointService.getUserPoint(userId);

        // then
        assertEquals(expected, result);
    }


    @Test
    @DisplayName("유저 ID로 포인트 충전,사용과 같은 이용내역을 조회한다.")
    void SelectUserId_ReturnPointHistory() {
        long userId = 1L;
        List<PointHistory> expected = List.of(
                new PointHistory(1L,userId, 1000L, TransactionType.CHARGE, Instant.now().toEpochMilli()),
                new PointHistory(2L,userId, 500L, TransactionType.USE, Instant.now().toEpochMilli())
        );
        given(pointHistoryRepository.selectAllByUserId(userId)).willReturn(expected);

        // when
        List<PointHistory> result = pointService.getUserPointHistory(userId);

        // then
        assertEquals(expected, result);
    }
}
