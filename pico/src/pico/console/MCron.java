package pico.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pico.ServiceServlet;
import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.engine.SchedulerManager;
import pico.view.View;
import pico.view.Views;

// http://code.google.com/p/myschedule Âü°í
@WebController
public class MCron extends Console {
	private static final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
	
	@WebMethod
	public View list() throws ServletException, SchedulerException {
		List<JobWithTrigger> jobWithTriggerList = new ArrayList<JobWithTrigger>();
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		List<JobDetail> allJobDetails = getAllJobDetails(scheduler);
        for (JobDetail jobDetail : allJobDetails) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
                for (Trigger trigger : triggers) {
                        String triggerScheduleExpression = trigger.getClass().getName();
                        if (trigger instanceof SimpleTrigger) {
                                SimpleTrigger t = (SimpleTrigger)trigger;
                                if (t.getRepeatCount() == SimpleTrigger.REPEAT_INDEFINITELY)
                                        triggerScheduleExpression = "Repeat=FOREVER";
                                else
                                        triggerScheduleExpression = "Repeat=" + t.getRepeatCount();
                                triggerScheduleExpression += ", Interval=" + t.getRepeatInterval();
                        } else if (trigger instanceof CronTrigger) {
                                CronTrigger t = (CronTrigger)trigger;
                                triggerScheduleExpression = "Cron=" + t.getCronExpression();           
                        }
                        
                        boolean paused = isTriggerPaused(trigger, scheduler);
                        
                        JobWithTrigger jobWithTrigger = new JobWithTrigger();
                        jobWithTrigger.jobGroup = trigger.getJobKey().getGroup();
                        jobWithTrigger.jobName = trigger.getJobKey().getName();
                        jobWithTrigger.triggerGroup = trigger.getKey().getGroup();
                        jobWithTrigger.triggerName = trigger.getKey().getName();
                        jobWithTrigger.trigger = trigger;
                        jobWithTrigger.triggerScheduleExpression = triggerScheduleExpression;
                        jobWithTrigger.paused = paused;
                        jobWithTrigger.description = jobDetail.getJobDataMap().getString("description");
                        
                        jobWithTriggerList.add(jobWithTrigger);
                }
                
                List<JobExecutionContext> executionJobs = scheduler.getCurrentlyExecutingJobs();
                for (JobExecutionContext context : executionJobs) {
                	for (JobWithTrigger jobWithTrigger : jobWithTriggerList) {
                		if (context.getTrigger().getJobKey().equals(jobWithTrigger.getTrigger().getJobKey())
                				&& context.getTrigger().getKey().equals(jobWithTrigger.getTrigger().getKey())) {
                			jobWithTrigger.running = true;
                		}
                	}
                }
        }

        // Let's sort them.
        Collections.sort(jobWithTriggerList);
        
        req.setAttribute("items", jobWithTriggerList);
        return forward("/MCron/list.ftl");
	}
	
	private List<JobDetail> getAllJobDetails(Scheduler scheduler) throws SchedulerException {
            List<JobDetail> jobs = new ArrayList<JobDetail>();
            List<String> groups = scheduler.getJobGroupNames();
            for (String group : groups) {
                    Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(group));
                    for (JobKey key : keys) {
                            JobDetail jobDetail = scheduler.getJobDetail(key);
                            jobs.add(jobDetail);
                    }
            }
            return jobs;
	}
	
	private boolean isTriggerPaused(Trigger trigger, Scheduler scheduler) throws SchedulerException {
        TriggerState state = scheduler.getTriggerState(trigger.getKey());
        return state == TriggerState.PAUSED;
	}
	
	public static class JobWithTrigger implements Comparable<JobWithTrigger> {
		public String description;
		private String jobGroup;
		private String jobName;
		private String triggerGroup;
		private String triggerName;
        private Trigger trigger;
        private String triggerScheduleExpression;
        private boolean paused;
        private boolean running;
        
        public int compareTo(JobWithTrigger other) {
                return trigger.compareTo(other.getTrigger());
        }

		public Trigger getTrigger() {
			return trigger;
		}

		public String getJobGroup() {
			return jobGroup;
		}

		public String getJobName() {
			return jobName;
		}

		public String getTriggerGroup() {
			return triggerGroup;
		}

		public String getTriggerName() {
			return triggerName;
		}

		public String getTriggerScheduleExpression() {
			return triggerScheduleExpression;
		}

		public boolean isPaused() {
			return paused;
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	@WebMethod
    public void pause(String triggerGroup, String triggerName) throws ServletException, SchedulerException
    {
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
    }
	
	@WebMethod
    public void resume(String triggerGroup, String triggerName) throws ServletException, SchedulerException
    {
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
    }
	
	@WebMethod
    public void unschedule(String triggerGroup, String triggerName) throws ServletException, SchedulerException
    {
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		if (trigger == null) {
            throw new SchedulerException("Trigger " + triggerKey + " not found.");
		}
		scheduler.unscheduleJob(triggerKey);
		logger.debug("Unscheduled trigger name=" + triggerName + ", group=" + triggerGroup);
    }
	
	@WebMethod
    public void delete(String jobGroup, String jobName) throws ServletException, SchedulerException
    {
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
            throw new SchedulerException("Job " + jobDetail + " not found.");
		}
		scheduler.deleteJob(jobKey);
		logger.debug("Deleted jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");
    }
	
	@WebMethod
    public void run(String jobGroup, String jobName) throws ServletException, SchedulerException
    {
		Scheduler scheduler = SchedulerManager.getFactory().getScheduler();
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
            throw new SchedulerException("Job " + jobDetail + " not found.");
		}
		scheduler.triggerJob(jobKey);
		logger.debug("Run jobName=" + jobName + ", jobGroup=" + jobGroup + " now.");
    }
}
