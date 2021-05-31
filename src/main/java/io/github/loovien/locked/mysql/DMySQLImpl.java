package io.github.loovien.locked.mysql;

import io.github.loovien.locked.DLocked;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DMySQLImpl extends DLocked {
    private final DataSource dataSource;

    private final String lockKey;

    private Connection connection;

    private PreparedStatement statement;

    private final ExecutorService executors = Executors.newSingleThreadExecutor();

    /**
     * FOR UPDATE 查询
     */
    private final String SQL_SELECT = "SELECT locked_key FROM d_locked WHERE locked_key = ? FOR UPDATE";

    /**
     * 没有创建， 先创建
     */
    private final String SQL_INSERT = "INSERT INTO d_locked (locked_key) VALUES (?)";

    public DMySQLImpl(DataSource dataSource, String lockKey) {
        this.dataSource = dataSource;
        this.lockKey = lockKey;
    }

    @Override
    public void lock() {
        for (; ; ) { // 数据库时里没有记录， 先创建记录， 这里可能有多个线程同时操作 ，但不用担心， 只有一个线程能创建成功。
            try {
                connection = this.dataSource.getConnection();
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(SQL_SELECT);
                statement.setString(1, this.lockKey);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) { // 当前事务拿到了锁
                    return;
                }
                closeIt(statement, resultSet);
                statement = connection.prepareStatement(SQL_INSERT);
                statement.setString(1, this.lockKey);
                statement.execute();
            } catch (SQLException throwables) {
                log.error("insert lock key {} failure.", lockKey, throwables);
            } finally {
                closeIt(connection, statement);
            }
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Future<Boolean> future = executors.submit(() -> {
            lock();
            return true;
        });
        try {
            return future.get(time, unit);
        } catch (Exception e) {
            log.error("try lock {} failure ", lockKey, e);
        }
        return false;
    }

    @Override
    @SneakyThrows
    public void unlock() {
        connection.commit();
        connection.close();
    }
}
