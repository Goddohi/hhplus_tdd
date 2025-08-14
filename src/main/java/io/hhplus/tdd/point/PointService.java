package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;


@Service
public class PointService {

    private final UserPointRepository userPointRepository;

    PointService(UserPointRepository userPointRepository){
        this.userPointRepository = userPointRepository;
    }

    public UserPoint getUserPoint(Long userId) {
        return userPointRepository.selectById(userId);
    }


}
