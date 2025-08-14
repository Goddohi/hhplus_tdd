package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PointService {

    private UserPointRepository userPointRepository;
    private PointHistoryRepository pointHistoryRepository;

    PointService(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public UserPoint getUserPoint(Long userId) {
        return userPointRepository.selectById(userId);
    }


    public List<PointHistory> getUserPointHistory(long id) {
        return pointHistoryRepository.selectAllByUserId(id);
    }
}
