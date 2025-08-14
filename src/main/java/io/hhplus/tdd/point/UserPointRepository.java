package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointRepository {


    private final UserPointTable userPointTable;

    UserPointRepository(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint insertOrUpdate(long id, long amount) {
        return userPointTable.insertOrUpdate(id,amount);
    }

}
