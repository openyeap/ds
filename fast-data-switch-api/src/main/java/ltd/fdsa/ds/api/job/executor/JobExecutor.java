package ltd.fdsa.ds.api.job.executor;


import lombok.extern.slf4j.Slf4j;
import ltd.fdsa.ds.api.job.coordinator.Coordinator;
import ltd.fdsa.ds.api.job.handler.JobHandler;
import ltd.fdsa.ds.api.job.log.JobFileAppender;
import ltd.fdsa.ds.api.job.thread.JobThread;
import ltd.fdsa.ds.api.job.thread.TriggerCallbackThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 客户端执行器
 */
@Slf4j
public class JobExecutor {
    // ---------------------- job handler repository ----------------------
    private static ConcurrentMap<String, JobHandler> jobHandlerRepository = new ConcurrentHashMap<String, JobHandler>();
    // ---------------------- job thread repository ----------------------
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    private final String appName;
    private final String ip;
    private final int port;
    private final String accessToken;
    private final String logPath;
    private final int logRetentionDays;

    public JobExecutor(Properties properties) {
        this.appName = properties.getProperty("name", "");
        this.ip = properties.getProperty("ip");
        this.port = Integer.parseInt(properties.getProperty("port", "8080"));
        this.logPath = properties.getProperty("log_path", "./logs");
        this.logRetentionDays = Integer.parseInt(properties.getProperty("log_days", "7"));
        this.accessToken = properties.getProperty("access_token", "");
    }


    /**
     * 注册本地Job Handler
     */
    public static JobHandler registerJobHandler(String name, JobHandler jobHandler) {
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static JobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    public static JobThread startJob(int jobId, JobHandler handler, String... reasons) {
        //如果job已经运行，需要停止运行
        stopJob(jobId, reasons);
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        return jobThreadRepository.put(jobId, newJobThread);
    }

    public static void stopJob(int jobId, String... removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(String.join("\n", removeOldReason));
            oldJobThread.interrupt();
        }
    }

    public static JobThread loadJobThread(int jobId) {
        JobThread jobThread = jobThreadRepository.get(jobId);
        return jobThread;
    }


    public void start() throws Exception {
        // init logpath
        JobFileAppender.initLogPath(logPath);



        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server todo

    }

    public void destroy() {
        // destory executor-server

        // destory jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : jobThreadRepository.entrySet()) {
                stopJob(item.getKey(), "web container destroy and kill the job.");
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();

        // destory TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();
    }

}
