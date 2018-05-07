package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAwsInstanceMetrics extends Thread{
	
	static int runningInstanceCount;
	static String[][] runningInstance;

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		GetAwsInstanceMetrics getRunningInstanceDetails = new GetAwsInstanceMetrics();
		
		try {
//			runningInstance = getRunningInstanceDetails.getRunningInstanceCount();
			getRunningInstanceDetails.getRunningInstanceCount();
			GetAwsInstanceMetrics[] getAwsInstanceMetrics = new GetAwsInstanceMetrics[runningInstanceCount];
			for(int i=0;i<runningInstanceCount;i++)
		        {
		        	for(int j=0;j<4;j++)
		        	{
		        		System.out.println(runningInstance[i][j]);
		        	}
		        }
			
			for(int i=0;i<runningInstanceCount;i++)
		        {
				getAwsInstanceMetrics[i] = new GetAwsInstanceMetrics();
				getAwsInstanceMetrics[i].setName(""+i);
		        }
			
			for(int i=0;i<runningInstanceCount;i++)
		        {
				getAwsInstanceMetrics[i].start();
		        }
				
		}catch(Exception e) {
			
		}
		

	}
	
	
//	private void getElasticAwsInstance() throws IOException, JSONException {
//		
//		runningInstanceCount = getRunningInstanceCount();
//		getRunningInstance(runningInstanceCount);
//		for(int i=0;i<runningInstanceCount;i++)
//	        {
//	        	for(int j=0;j<4;j++)
//	        	{
//	        		System.out.println(runningInstance[i][j]);
//	        	}
//	        }
//		
//	}
	
	private void getRunningInstanceCount() throws IOException, JSONException
	{
		String command[]= {"curl","-XGET","http://localhost:9200/awsinstance/_search","-H","Content-Type:application/json","-d","{\"size\":0,\"_source\":{\"excludes\":[]},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}}},\"stored_fields\":[\"*\"],\"script_fields\":{},\"docvalue_fields\":[\"post_date\"],\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"query\":\"Status:running\",\"analyze_wildcard\":true,\"default_field\":\"*\"}},{\"range\":{\"post_date\":{\"gte\":1514764800000,\"lte\":1546300799999,\"format\":\"epoch_millis\"}}}],\"filter\":[],\"should\":[],\"must_not\":[]}}}"};
		
	    ProcessBuilder builder = new ProcessBuilder(command);
	    //Map<String, String> environ = builder.environment();

	    final Process process = builder.start();
	    InputStream is = process.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;
	    StringBuffer response = new StringBuffer();
	    while ((line = br.readLine()) != null) {
	    	response.append(line);
	    }
	    br.close();
//		System.out.println(response.toString());
        JSONObject jsonObject = new JSONObject(response.toString());
//        System.out.println(jsonObject.getJSONObject("aggregations").getJSONObject("1").getInt("value"));
        runningInstanceCount = jsonObject.getJSONObject("aggregations").getJSONObject("1").getInt("value");
        System.out.println(runningInstanceCount);
        getRunningInstance(runningInstanceCount);
//        return getRunningInstance(runningInstanceCount);
	}
	
	private void getRunningInstance(int runningInstanceCount) throws IOException, JSONException
	{
		System.out.println(runningInstanceCount);
		
		String command[]= {"curl","-XGET","http://localhost:9200/awsinstance/_search","-H","Content-Type:application/json","-d","{\"size\":0,\"_source\":{\"excludes\":[]},\"aggs\":{\"2\":{\"terms\":{\"field\":\"instanceID.keyword\",\"size\":"+runningInstanceCount+",\"order\":{\"1\":\"desc\"}},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}},\"3\":{\"terms\":{\"field\":\"key.keyword\",\"size\":5,\"order\":{\"1\":\"desc\"}},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}},\"4\":{\"terms\":{\"field\":\"region.keyword\",\"size\":5,\"order\":{\"1\":\"desc\"}},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}},\"5\":{\"terms\":{\"field\":\"secret.keyword\",\"size\":5,\"order\":{\"1\":\"desc\"}},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}},\"7\":{\"terms\":{\"field\":\"Status.keyword\",\"size\":5,\"order\":{\"1\":\"desc\"}},\"aggs\":{\"1\":{\"cardinality\":{\"field\":\"instanceID.keyword\"}}}}}}}}}}}}},\"stored_fields\":[\"*\"],\"script_fields\":{},\"docvalue_fields\":[\"post_date\"],\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"query\":\"Status:running\",\"analyze_wildcard\":true,\"default_field\":\"*\"}},{\"range\":{\"post_date\":{\"gte\":1514764800000,\"lte\":1546300799999,\"format\":\"epoch_millis\"}}}],\"filter\":[],\"should\":[],\"must_not\":[]}}}"};
		
	    ProcessBuilder builder = new ProcessBuilder(command);
	    //Map<String, String> environ = builder.environment();

	    final Process process = builder.start();
	    InputStream is = process.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;
	    StringBuffer response = new StringBuffer();
	    while ((line = br.readLine()) != null) {
	    	response.append(line);
	    }
	    br.close();
		System.out.println(response.toString());
        JSONObject jsonObject = new JSONObject(response.toString());
        
        runningInstance = new String[runningInstanceCount][4];
        
        for(int i=0;i<runningInstanceCount;i++)
        {
        	runningInstance[i][0]=jsonObject.getJSONObject("aggregations").getJSONObject("2").getJSONArray("buckets").getJSONObject(i).getString("key");
        	runningInstance[i][1]=jsonObject.getJSONObject("aggregations").getJSONObject("2").getJSONArray("buckets").getJSONObject(i).getJSONObject("3").getJSONArray("buckets").getJSONObject(0).getString("key");
        	runningInstance[i][2]=jsonObject.getJSONObject("aggregations").getJSONObject("2").getJSONArray("buckets").
            						getJSONObject(i).getJSONObject("3").getJSONArray("buckets").
            						getJSONObject(0).getJSONObject("4").getJSONArray("buckets").getJSONObject(0).getString("key");
        	runningInstance[i][3]=jsonObject.getJSONObject("aggregations").getJSONObject("2").getJSONArray("buckets").
            						getJSONObject(i).getJSONObject("3").getJSONArray("buckets").
            						getJSONObject(0).getJSONObject("4").getJSONArray("buckets").
            						getJSONObject(0).getJSONObject("5").getJSONArray("buckets").getJSONObject(0).getString("key");
        	
        	
        }
//        String runningInstance=jsonObject.getJSONObject("aggregations").getJSONObject("2").getJSONArray("buckets").getJSONObject(0).getString("key");
//        System.out.println(runningInstance);
        
//        for(int i=0;i<runningInstanceCount;i++)
//        {
//        	for(int j=0;j<4;j++)
//        	{
//        		System.out.println(runningInstance[i][j]);
//        	}
//        }
        
//        return runningInstance;
	}
	
	public void run() {
	
		get_avgCPUUtilization avgCPUUtilization = new get_avgCPUUtilization();
		Thread t1 = new Thread(avgCPUUtilization); 
		t1.setName(Thread.currentThread().getName());
		t1.start();
		
		
		
	
	}
	

}
