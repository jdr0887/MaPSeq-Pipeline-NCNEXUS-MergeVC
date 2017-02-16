package edu.unc.mapseq.executor.ncnexus.mergevc;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NCNEXUSMergeVCWorkflowExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(NCNEXUSMergeVCWorkflowExecutorService.class);

    private final Timer mainTimer = new Timer();

    private NCNEXUSMergeVCWorkflowExecutorTask task;

    private Long period = 5L;

    public NCNEXUSMergeVCWorkflowExecutorService() {
        super();
    }

    public void start() throws Exception {
        logger.info("ENTERING start()");
        long delay = 1 * 60 * 1000;
        mainTimer.scheduleAtFixedRate(task, delay, period * 60 * 1000);
    }

    public void stop() throws Exception {
        logger.info("ENTERING stop()");
        mainTimer.purge();
        mainTimer.cancel();
    }

    public NCNEXUSMergeVCWorkflowExecutorTask getTask() {
        return task;
    }

    public void setTask(NCNEXUSMergeVCWorkflowExecutorTask task) {
        this.task = task;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

}
