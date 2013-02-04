package com.wiley.contentindex;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.plugin.PluginDeployer;
import com.dotmarketing.plugin.business.PluginAPI;
import com.dotmarketing.quartz.CronScheduledTask;
import com.dotmarketing.quartz.QuartzUtils;

/**
 * Plugin deployer to set-up index job, and check site index set-up. 
 */
public class ContentIndexPluginDeployer implements PluginDeployer {
	
	public final static String VERSION = "v1.0.41";
	
	protected static Logger logger = Logger.getLogger(ContentIndexPluginDeployer.class);
	
	private PluginAPI pluginAPI = APILocator.getPluginAPI();
	private String pluginId = "com.wiley.contentindex"; 
	
	/** 
	* This method will execute the first time the plugin deploys.
	* 
	* @return 
	*/ 
	@Override
	public boolean deploy() {
		
		logger.debug("deploying version " + VERSION);
		
		startJob();
		
		return true;
	}
	
	/** 
	* If the manifest version of the plugin is higher then 
	* the current build number of the plugin is higher. 
	* 
	* @return 
	*/ 
	@Override
	public boolean redeploy(String version) {

		logger.debug("re-deploying version " + VERSION);

		startJob();
		
		return true;
	}
	
	/**
	 * Scheduler the custom job...
	 */
	private void startJob() {
		
		// TODO ensure start/re-start is bullet-proof
		
		// TODO need to include job parameters
		
		// TODO undeploy and deploy!!
		
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("param1", "value1");
			params.put("param2", "value2");
			
			CronScheduledTask cronScheduledTask = 
				new CronScheduledTask(
					pluginAPI.loadProperty(pluginId, "com.wiley.contentindex.JOB_NAME"), 
					pluginAPI.loadProperty(pluginId, "com.wiley.contentindex.JOB_GROUP"), 
					pluginAPI.loadProperty(pluginId, "com.wiley.contentindex.JOB_DESCRIPTION"), 
					pluginAPI.loadProperty(pluginId, "com.wiley.contentindex.JOB_CLASS"), 
					new Date(), 
					null, 
					CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW, 
					params, 
					pluginAPI.loadProperty(pluginId, "com.wiley.contentindex.CRON_EXPRESSION"));
			
			QuartzUtils.scheduleTask(cronScheduledTask);

		}catch(Exception e){
			logger.error("exception scheduling job task. ", e);
		}
		
		
		/*
		boolean isNew = false;
		Scheduler sched = null;
		try {
			sched = QuartzUtils.getStandardScheduler();
		} catch (SchedulerException e) {
			logger.error("scheduler exception getting standard scheduler ", e);
			return;
		}
		JobDetail job = null;
		
		try {
			try {
				if ((job = sched.getJobDetail(JOB_NAME, JOB_GROUP)) == null) {
					job = new JobDetail(JOB_NAME, JOB_GROUP, JOB_CLASS);
					job.setDescription(JOB_NAME);
					job.setJobClass(JOB_CLASS);
					isNew = true;
				}
			} catch (SchedulerException se) {
				
				logger.error("scheduler exception creating job detail ", se);
				
				sched.deleteJob(JOB_NAME, JOB_GROUP);
				job = new JobDetail(JOB_NAME, JOB_GROUP, JOB_CLASS);
				isNew = true; 
			}
			
			CronTrigger trigger = new CronTrigger(
					TRIGGER_NAME, TRIGGER_GROUP, JOB_NAME, JOB_GROUP, 
					new Date(), null, CRON_EXPRESSION);
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
			sched.addJob(job, true);

			if (isNew) {
				logger.debug("scheduling job");
				sched.scheduleJob(trigger);
			} else {
				logger.debug("rescheduling job");
				sched.rescheduleJob(TRIGGER_NAME, TRIGGER_GROUP, trigger);
			}
			
		} catch (Exception e) {
			logger.error("exception enabling job ", e);
		}
		*/
	}
	
}