package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    PointHistoryRepository(PointHistoryTable pointHistoryTable) {
        this.pointHistoryTable = pointHistoryTable;
    }

    public List<PointHistory> selectAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis){
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }
}
