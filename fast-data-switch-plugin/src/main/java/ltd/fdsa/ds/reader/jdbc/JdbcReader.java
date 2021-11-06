package ltd.fdsa.ds.reader.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.model.Column;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Reader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class JdbcReader implements Reader {
    String driver;
    String url;
    String user;
    String password;
    String sql;
    Map<String, String> scheme;
    Connection conn;

    @Override
    public void init() {
        try {
            this.url = this.config().getString("url");
            this.driver = this.config().getString("driver");
            this.user = this.config().getString("username");
            this.password = this.config().getString("password");
            this.sql = this.config().getString("sql");
            this.scheme = new HashMap<>(64);
            Class.forName(driver);
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
//                return Result.error(e);
        } catch (ClassNotFoundException e) {
//                return Result.error(e);
        }
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(String.format("select * from (%s) a where 1=2", sql));
            ResultSetMetaData metaData = rs.getMetaData(); // 获取键名
            int columnCount = metaData.getColumnCount(); // 获取行的数量
            for (int i = 1; i <= columnCount; i++) {
                scheme.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
            }
//                return Result.success();
        } catch (SQLException e) {
            log.error("JdbcSourcePipeline.getColumn", e);
//                return Result.error(e);
        }

    }

    @Override
    public void collect(Record... records) {
        if (this.isRunning()) {
            for (var item : this.nextSteps()) {
                item.collect(records);
            }
        }
    }

    @Override
    public Map<String, String> scheme() {
        return this.scheme;
    }

    @Override
    public void start() {
        while (this.isRunning()) {
            List<Record> list = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    Record record = new Record();
                    for (var key : this.scheme.keySet()) {
                        record.add(new Column(key, rs.getObject(key)));
                    }
                    list.add(record);
                }
                rs.close();
            } catch (SQLException e) {
                log.error("JdbcSourcePipeline.process", e);
            }
            this.collect(list.toArray(new Record[0]));
        }
    }
}
