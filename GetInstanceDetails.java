package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;

public class GetInstanceDetails {
	
//	BasicAWSCredentials credentials = null;
	AWSCredentials credentials = null;
	String instanceID; 
	String InstanceType; 
	String Status; 
	String MonitoringStatus;
	String[] RegionName;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GetInstanceDetails getElasticAwsCredentials = new GetInstanceDetails();
		GetInstanceDetails getAwsRegion = new GetInstanceDetails();
//		GetInstanceDetails getAwsInstance = new GetInstanceDetails();
		
		String[] RegionName = getAwsRegion.getAwsRegion();
		
		
		try {
			getElasticAwsCredentials.getElasticAwsCredentials(RegionName);
		}catch(Exception e) {
			
		}
		
	}
	
	
private String[] getAwsRegion() {
		

        try {

//            credentials = new ProfileCredentialsProvider("default").getCredentials();
//            System.out.println(credentials);
        	credentials = new BasicAWSCredentials("Key", "secret");
        	AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion("region")
                    .build();
        	
        	DescribeRegionsResult regions = ec2.describeRegions();
    		List<Region> regionList = regions.getRegions();
    		
    		int RegionCounter = 0;
    		RegionName = new String[regionList.size()];
    		for (Region r : regionList) {
    			String name = r.getRegionName();
    			System.out.println(name);
    			RegionName[RegionCounter]=name;
    			RegionCounter = RegionCounter+1;
    		}
    		
    		
        } catch (Exception e) {

            throw new AmazonClientException(

                    "Cannot load the credentials from the credential profiles file. " +

                    "Please make sure that your credentials file is at the correct " +

                    "location (/root/.aws/credentials), and is in valid format.",

                    e);

        }
        
        System.out.println("Login done");
        return RegionName;
        
	}
	
		
	private void getElasticAwsCredentials(String[] RegionName) throws IOException, JSONException {
		String command[]= {"curl","-XGET","localhost:9200/accountdetails/_search?q=Status:active"};
		
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
//        System.out.println(jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("user"));
        System.out.println(jsonObject.getJSONObject("hits").getJSONArray("hits").length());
        int length=jsonObject.getJSONObject("hits").getJSONArray("hits").length();
        for(int i=0;i<length;i++)
        {
        	String key = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("key");
        	String secret = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("secret");
        	System.out.println(key+" : "+secret);        	
        	getAwsInstance(RegionName,key,secret);
        }
	}
	
	
	private void getAwsInstance(String[] RegionName, String key, String secret) {
		try {

//          credentials = new ProfileCredentialsProvider("default").getCredentials();
//          System.out.println(credentials);
      	credentials = new BasicAWSCredentials(key, secret);
      	
      	for(int i=0; i<RegionName.length; i++)
		{
			System.out.println(RegionName[i]);
			AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
	                  .withCredentials(new AWSStaticCredentialsProvider(credentials))
	                  .withRegion(RegionName[i])
	                  .build();
			
			boolean done = false;
			  
	          DescribeInstancesRequest request = new DescribeInstancesRequest();
	          while(!done) {
	              DescribeInstancesResult response = ec2.describeInstances(request);
	  
	              for(Reservation reservation : response.getReservations()) {
	                  for(Instance instance : reservation.getInstances()) {
	                	  System.out.println("{instanceID :"+instance.getInstanceId()+", InstanceType: "+instance.getInstanceType()+", Status: "+instance.getState().getName()+", MonitoringStatus: "+instance.getMonitoring().getState()+"}");
	                	  instanceID=instance.getInstanceId(); 
	                	  InstanceType=instance.getInstanceType(); 
	                	  Status=instance.getState().getName(); 
	                	  MonitoringStatus=instance.getMonitoring().getState();
	                	  
	                	  try {
	                		  postElasticAwsInstance(key, secret, RegionName[i], instanceID, InstanceType, Status, MonitoringStatus);
	                	  }catch(Exception e) {
	                		  
	                	  }
	                	  
	                	  
	                	                     
	                  }
	              }
	  
	              request.setNextToken(response.getNextToken());
	  
	              if(response.getNextToken() == null) {
	                  done = true;
	              }
	          }
		}    	  	
      	
      } catch (Exception e) {

          throw new AmazonClientException(

                  "Cannot load the credentials from the credential profiles file. " +

                  "Please make sure that your credentials file is at the correct " +

                  "location (/root/.aws/credentials), and is in valid format.",

                  e);

      }
      
      System.out.println("Login done");
	}
	
	
	private void postElasticAwsInstance(String key, String secret, String region, String instanceID, String InstanceType, String Status, String MonitoringStatus) throws IOException {
		
		Date now = new Date();
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
	    SimpleDateFormat DATE_FORMAT_time = new SimpleDateFormat("HH:mm:ss");
	    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String date = DATE_FORMAT.format(now)+"T"+DATE_FORMAT_time.format(now);
//		System.out.println(date);
		String command[]= {"curl","-XPOST","localhost:9200/awsinstance/awsinstance/","-H","Content-Type:application/json","-d","{\"post_date\":\""+date+"\",\"key\":\""+key+"\",\"secret\":\""+secret+"\",\"region\":\""+region+"\",\"instanceID\":\""+instanceID+"\",\"InstanceType\":\""+InstanceType+"\",\"Status\":\""+Status+"\",\"MonitoringStatus\":\""+MonitoringStatus+"\"}"};
		ProcessBuilder builder = new ProcessBuilder(command);
	    builder.start();
	}
	
	

}
