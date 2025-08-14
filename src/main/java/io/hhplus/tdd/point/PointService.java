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

    public UserPoint getUserPoint(long userId) {
        return userPointRepository.selectById(userId);
    }


    public List<PointHistory> getUserPointHistory(long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }

    public UserPoint chargeUserPoint(long userId, long amount){
        if(amount <= 0 ) {
            // throw new IllegalArgumentException("0원 초과의 금액만 충전이 가능합니다."); //간단하게 충전을 하지않는다.
            return getUserPoint(userId);
        }
        //충전 이력
        insertUserPointHistory( userId,
                                amount,
                                TransactionType.CHARGE,
                                System.currentTimeMillis());

        return userPointRepository.insertOrUpdate(userId,amount);
    }

    public PointHistory insertUserPointHistory(long userId, long amount,TransactionType transactionType, long updateMillis){
        return pointHistoryRepository.insert(userId,amount,transactionType,updateMillis);
    }
}
