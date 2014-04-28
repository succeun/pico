package pico.engine;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import pico.ControllerContext;
import pico.CronMethod;

public class SchedulerManager {
	private static SchedulerFactory schedulerFactory;
	
	static {
		Properties props = new Properties();
		InputStream in = ControllerMapper.class.getClassLoader().getResourceAsStream("quartz.properties");
		try {
			props.load(in);
			schedulerFactory = new StdSchedulerFactory(props);
		} catch (Exception e) {
			schedulerFactory = new StdSchedulerFactory();
		}
	}
	
	public static SchedulerFactory getFactory() {
		return schedulerFactory;
	}
	
	public static void createJob(ServletConfig config, ControllerContext controllerContext,
			Class<?> controllerClass, Object controller, MethodMapper cron) 
			throws SchedulerException, IllegalAccessException, InstantiationException, ServletException, ParseException {
		String name = cron.getMethod().getAnnotation(CronMethod.class).name();
		if (name == null || name.length() == 0) {
			name = controllerClass.getName() + "#" + cron.getMethodName();
		}
		String group = cron.getMethod().getAnnotation(CronMethod.class).group();
		if (group == null || group.length() == 0) {
			group = "DEFAULT";
		}

		String description = cron.getMethod().getAnnotation(CronMethod.class)
				.description();
		if (description == null || description.length() == 0) {
			description = "";
		}
		String expression = cron.getMethod().getAnnotation(CronMethod.class)
				.expression();
		Scheduler scheduler = getFactory().getScheduler();
		scheduler.start();

		JobDetail job = org.quartz.JobBuilder.newJob(JobBroker.class)
				.withIdentity(name, group).build();

		job.getJobDataMap().put("servletConfig", config);
		job.getJobDataMap().put("controllerContext", controllerContext);
		job.getJobDataMap().put("controller", controller);
		job.getJobDataMap().put("methodMapper", cron);
		job.getJobDataMap().put("description", description);

		CronTrigger trigger = org.quartz.TriggerBuilder
				.newTrigger()
				.withIdentity(name, group)
				.withSchedule(
						org.quartz.CronScheduleBuilder.cronSchedule(expression))
				.build();
		scheduler.scheduleJob(job, trigger);
	}
	
	public static class JobBroker implements Job {
		public void execute(JobExecutionContext context) throws JobExecutionException {
			try {
				JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
				Object controller = jobDataMap.get("controller");
				MethodMapper cron = (MethodMapper) jobDataMap.get("methodMapper");
				ControllerContext controllerContext = (ControllerContext) jobDataMap.get("controllerContext");
				ServletConfig config = (ServletConfig) jobDataMap.get("servletConfig");
				
				cron.invoke(controller, config, config.getServletContext(), controllerContext, null, null);
			} catch (Exception e) {
				throw new JobExecutionException(e);
			}
		}
	}

	public static void init() throws SchedulerException {
		if (schedulerFactory != null) {
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.clear();
			scheduler.shutdown();
		}
	}
}
