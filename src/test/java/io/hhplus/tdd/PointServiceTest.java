package io.hhplus.tdd;


import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    PointService pointService;

    @Mock
    UserPointRepository userPointRepository;


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
}
