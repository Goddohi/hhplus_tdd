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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

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
                new PointHistory(1L,userId, 1000L, TransactionType.CHARGE, Instant.parse("2025-08-15T00:00:00Z").toEpochMilli()),
                new PointHistory(2L,userId, -500L, TransactionType.USE, Instant.parse("2025-08-15T00:00:00Z").toEpochMilli())
        );
        given(pointHistoryRepository.selectAllByUserId(userId)).willReturn(expected);

        // when
        List<PointHistory> result = pointService.getUserPointHistory(userId);

        // then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("충전 성공: 양수 금액이면 포인트 업데이트 및 CHARGE 이력 생성")
    void charge_success_records_history_and_updates_point() {
        // Given
        long userId = 1L;
        long amount = 1000L;

        //
        UserPoint updated = new UserPoint(userId, 10000L, System.currentTimeMillis());
        given(userPointRepository.insertOrUpdate(userId, amount)).willReturn(updated);

        // When
        UserPoint result = pointService.chargeUserPoint(userId, amount);

        // Then
        // 1) 이력 insert가 CHARGE 타입으로 호출되었는지
        then(pointHistoryRepository).should(times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());

        // 2) 포인트 저장소 업데이트가 호출되었는지
        then(userPointRepository).should(times(1))
                .insertOrUpdate(userId, amount);

        // 3) 서비스 반환값 검증(더미로 준 값이 그대로 반환되어야 함)
        assertThat(result).isEqualTo(updated);
    }

    @Test
    @DisplayName("충전 실패: 음수 금액이면 업데이트/이력 생성 없이 현재 포인트만 반환")
    void charge_fail_negative_amount_no_history_no_update_returns_current_point() {
        // Given
        long userId = 2L;
        long negativeAmount = -500L;

        UserPoint current = new UserPoint(userId, 5000L, System.currentTimeMillis());
        given(userPointRepository.selectById(userId)).willReturn(current);

        // When
        UserPoint result = pointService.chargeUserPoint(userId, negativeAmount);

        // Then
        // 이력/업데이트는 호출되지 않아야 함
        then(pointHistoryRepository).shouldHaveNoInteractions();
        then(userPointRepository).shouldHaveNoMoreInteractions();

        // 현재 포인트가 그대로 반환
        assertThat(result).isEqualTo(current);
    }

    @Test
    @DisplayName("충전 실패: 0원은 실패 처리—이력/업데이트 없이 현재 포인트 반환")
    void charge_fail_zero_amount_no_history_no_update_returns_current_point() {
        // Given
        long userId = 3L;
        long zero = 0L;

        UserPoint current = new UserPoint(userId, 7000L, System.currentTimeMillis());
        given(userPointRepository.selectById(userId)).willReturn(current);

        // When
        UserPoint result = pointService.chargeUserPoint(userId, zero);

        // Then
        //
        then(pointHistoryRepository).shouldHaveNoInteractions();
        then(userPointRepository).shouldHaveNoMoreInteractions();

        // 현재 포인트가 그대로 반환
        assertThat(result).isEqualTo(current);
    }
    @Test
    @DisplayName("포인트 이용이력이 insert가 되는지 확인한다.")
    void insert_delegates_and_returns_value_charge() {
        // Given
        long userId = 1L;
        long amount = 1000L;
        TransactionType type = TransactionType.CHARGE;
        long now = Instant.parse("2025-08-15T00:00:00Z").toEpochMilli();

        PointHistory saved = new PointHistory(10L, userId, amount, type, now);
        given(pointHistoryRepository.insert(userId, amount, type, now)).willReturn(saved);

        // When
        PointHistory result = pointHistoryRepository.insert(userId, amount, type, now);

        // Then
        // 1) 정확히 한 번, 같은 인자들로 호출되었는지
        then(pointHistoryRepository).should(times(1)).insert(userId, amount, type, now);
        then(pointHistoryRepository).shouldHaveNoMoreInteractions();

        // 2) 반환값이 그대로 전달되는지
        assertThat(result).isEqualTo(saved);
    }

    @Test
    @DisplayName("사용 성공: 잔고 적은 사용 금액이면 포인트 업데이트 및 CHARGE 이력 생성")
    void use_success_records_history_and_updates_point() {
        // Given
        long userId = 1L;
        long amount = -1000L;

        //
        UserPoint current = new UserPoint(userId, 10000L, System.currentTimeMillis());
        UserPoint updated = new UserPoint(userId, 9000L, System.currentTimeMillis());
        given(userPointRepository.selectById(userId)).willReturn(current);
        given(userPointRepository.insertOrUpdate(userId, amount)).willReturn(updated);

        // When
        UserPoint result = pointService.useUserPoint(userId, amount);

        // Then
        // 1) 이력 insert가 Use 타입으로 호출되었는지
        then(pointHistoryRepository).should(times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.USE), anyLong());

        // 2) 포인트 저장소 업데이트가 호출되었는지
        then(userPointRepository).should(times(1))
                .insertOrUpdate(userId, amount);

        // 3) 서비스 반환값 검증(더미로 준 값이 그대로 반환되어야 함)
        assertThat(result).isEqualTo(updated);
    }

    @Test
    @DisplayName("사용 실패: 양수 금액이면 업데이트/이력 생성 없이 현재 포인트만 반환")
    void use_fail_positive_amount_no_history_no_update_returns_current_point() {
        // Given
        long userId = 2L;
        long positiveAmount = 500L;

        UserPoint current = new UserPoint(userId, 5000L, System.currentTimeMillis());
        given(userPointRepository.selectById(userId)).willReturn(current);

        // When
        UserPoint result = pointService.useUserPoint(userId, positiveAmount);

        // Then
        // 이력/업데이트는 호출되지 않아야 함
        then(pointHistoryRepository).shouldHaveNoInteractions();
        then(userPointRepository).shouldHaveNoMoreInteractions();

        // 현재 포인트가 그대로 반환
        assertThat(result).isEqualTo(current);
    }

    @Test
    @DisplayName("사용 실패: 돈이 부족하여 사용불가 업데이트/이력 생성 없이 현재 포인트만 반환")
    void use_fail_no_history_no_update_returns_current_point() {
        // Given
        long userId = 2L;
        long negativeAmount = -500L;

        UserPoint current = new UserPoint(userId, 200L, System.currentTimeMillis());
        given(userPointRepository.selectById(userId)).willReturn(current);

        // When
        UserPoint result = pointService.useUserPoint(userId, negativeAmount);

        // Then
        // 이력/업데이트는 호출되지 않아야 함
        then(pointHistoryRepository).shouldHaveNoInteractions();
        then(userPointRepository).shouldHaveNoMoreInteractions();

        // 현재 포인트가 그대로 반환
        assertThat(result).isEqualTo(current);
    }

}


