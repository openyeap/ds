package ltd.fdsa.switcher.plugin;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.switcher.core.job.enums.HttpCode;
import ltd.fdsa.switcher.core.job.model.Result;
import ltd.fdsa.switcher.core.model.Column;
import ltd.fdsa.switcher.core.model.Record;
import ltd.fdsa.switcher.core.pipeline.Reader;
import ltd.fdsa.switcher.core.config.Configuration;
import ltd.fdsa.switcher.core.pipeline.impl.AbstractPipeline;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class JdbcReader extends AbstractPipeline implements Reader {
    String driver;
    String url;
    String user;
    String password;
    String sql;
    Map<String, String> scheme;
    Connection conn;


    @Override
    public Result<String> init(Configuration configuration) {
        var result = super.init(configuration);
        if (result.getCode() == 200) {
            try {
                this.url = config.getString("url");
                this.driver = config.getString("driver");
                this.user = config.getString("username");
                this.password = config.getString("password");
                this.sql = config.getString("sql");
                this.scheme = new HashMap<>(64);
                Class.forName(driver);
                this.conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                return Result.error(e);
            } catch (ClassNotFoundException e) {
                return Result.error(e);
            }
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(String.format("select * from (%s) a where 1=2", sql));
                ResultSetMetaData metaData = rs.getMetaData(); // 获取键名
                int columnCount = metaData.getColumnCount(); // 获取行的数量
                for (int i = 1; i <= columnCount; i++) {
                    scheme.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
                }
                return Result.success();
            } catch (SQLException e) {
                log.error("JdbcSourcePipeline.getColumn", e);
                return Result.error(e);
            }
        }
        return Result.fail(HttpCode.EXPECTATION_FAILED, result.getMessage());
    }

    @Override
    public Map<String, String> scheme() {
        return this.scheme;
    }

    @Override
    public void start() {
        if (this.running.compareAndSet(false, true)) {
            while (this.isRunning()) {
                List<Record> list = new ArrayList<>();
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        Record record = new Record();
                        for (var key : this.scheme.keySet()) {
                            record.Add(new Column(key, rs.getObject(key)));
                        }
                        list.add(record);
                    }
                    // STEP 6: Clean-up environment
                    rs.close();
                } catch (SQLException e) {
                    log.error("JdbcSourcePipeline.process", e);
                }
                super.collect(list.toArray(new Record[0]));
            }
        }

    }

}
