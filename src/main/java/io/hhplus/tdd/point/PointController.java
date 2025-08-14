package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }
    /**
     * TODO - 특정 유저의 포인트 조회
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) {
        return pointService.getUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) {
        return pointService.getUserPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능
     * 1. 충전을 하고 나서 잔액 반환
     * 2. 충전을 한 이용내역 기록
     * 조건 - 0원초과인경우에만 충전할것
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id, @RequestBody long amount) {

        return pointService.chargeUserPoint(id,amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능
     * 1. 음수로 들어온다.
     * 2. 0원사용도 저장한다
     * 3. 잔고가 없을 경우 사용하지 않는다.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id, @RequestBody long amount) {
        return pointService.useUserPoint(id,amount);
    }
}
