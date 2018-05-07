package com.amazonaws.samples;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

public class get_avgCPUUtilization extends GetAwsInstanceMetrics implements Runnable {
	
	AWSCredentials credentials = null;

	private double getInstanceAverageLoad(AmazonCloudWatch cloudWatchClient, String instanceId, String region) throws IOException {

		long offsetInMilliseconds = 1000 * 10 * 60;
    	
    	
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace("AWS/EC2")
                .withPeriod(60*5)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
                .withMetricName("CPUUtilization")
                .withStatistics("Average", "Maximum")
                .withEndTime(new Date());
        GetMetricStatisticsResult getMetricStatisticsResult = cloudWatchClient.getMetricStatistics(request);
        		
        
        DecimalFormat format = new DecimalFormat("##.00");

        double avgCPUUtilization = 0;
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for (Object aDataPoint : dataPoint) {
            Datapoint dp = (Datapoint) aDataPoint;
            avgCPUUtilization = dp.getAverage();
            avgCPUUtilization = Double.parseDouble(format.format(avgCPUUtilization/300));
            System.out.println("Time: "+new Date()+", instanceId: "+instanceId+", avgCPUUtilization: "+avgCPUUtilization);

            this.postElasticAwsInstance(instanceId,avgCPUUtilization,region);
            
        }

        return avgCPUUtilization;
    }
	
	

	public void run() {
		// TODO Auto-generated method stub
		
//		runningInstance[Integer.parseInt(Thread.currentThread().getName())][0]
		
		try
		{
			credentials = new BasicAWSCredentials(runningInstance[Integer.parseInt(Thread.currentThread().getName())][1], runningInstance[Integer.parseInt(Thread.currentThread().getName())][3]);
			AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard()
	      		  .withCredentials(new AWSStaticCredentialsProvider(credentials))
	      		  .withRegion(runningInstance[Integer.parseInt(Thread.currentThread().getName())][2])
	                .build();
			this.getInstanceAverageLoad(cw,runningInstance[Integer.parseInt(Thread.currentThread().getName())][0],runningInstance[Integer.parseInt(Thread.currentThread().getName())][2]);
		}catch(Exception e) {
			
		}
		
	}
	
private void postElasticAwsInstance(String instanceID, double avgCPUUtilization, String region) throws IOException {
		
		Date now = new Date();
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
	    SimpleDateFormat DATE_FORMAT_time = new SimpleDateFormat("HH:mm:ss");
	    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String date = DATE_FORMAT.format(now)+"T"+DATE_FORMAT_time.format(now);
//		System.out.println(date);
		String command[]= {"curl","-XPOST","localhost:9200/avgcpuutilization/avgcpuutilization/","-H","Content-Type:application/json","-d","{\"post_date\":\""+date+"\",\"region\":\""+region+"\",\"instanceID\":\""+instanceID+"\",\"avgCPUUtilization\":\""+avgCPUUtilization+"\"}"};
		ProcessBuilder builder = new ProcessBuilder(command);
	    builder.start();
	}
	
	
}
