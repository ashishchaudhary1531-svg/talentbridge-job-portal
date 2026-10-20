package com.talentbridge.service;

import com.talentbridge.db.ActivityDao;
import com.talentbridge.db.JobDao;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Runs non-blocking audit writes and periodically closes expired vacancies. */
public final class BackgroundTaskManager implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(BackgroundTaskManager.class.getName());
    private final ExecutorService activityWorker = Executors.newSingleThreadExecutor(r -> new Thread(r, "portal-activity-worker"));
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "portal-expiry-worker"));
    private final ActivityDao activityDao;
    private final JobDao jobDao;

    public BackgroundTaskManager(ActivityDao activityDao, JobDao jobDao) {
        this.activityDao = activityDao;
        this.jobDao = jobDao;
        scheduler.scheduleWithFixedDelay(this::closeExpiredJobs, 1, 24, TimeUnit.HOURS);
    }

    public void recordActivity(String actorId, String description) {
        activityWorker.submit(() -> {
            try { activityDao.add(actorId, description); }
            catch (SQLException ex) { LOG.log(Level.WARNING, "Could not record portal activity", ex); }
        });
    }

    private void closeExpiredJobs() {
        try { jobDao.closeExpiredJobs(); }
        catch (SQLException ex) { LOG.log(Level.WARNING, "Could not close expired job postings", ex); }
    }

    @Override public void close() {
        scheduler.shutdown();
        activityWorker.shutdown();
        try {
            if (!activityWorker.awaitTermination(3, TimeUnit.SECONDS)) activityWorker.shutdownNow();
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) scheduler.shutdownNow();
        } catch (InterruptedException ex) {
            activityWorker.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
