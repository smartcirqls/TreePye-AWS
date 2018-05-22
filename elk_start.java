package test1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;


import org.json.JSONException;
import org.json.JSONObject;

public class Test1 {

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub

		String instance_role = "elasticsearch";
		String ip = "10.1.1.18";
		String status = "active";
		
		Test1 elk = new Test1();
		  try {
			 // elk.getInstanceDetails();
			 elk.postElasticInstance(instance_role, ip, status);
			//elk.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// Code for getting data from elastic
	
	/**private void getInstance() throws IOException, JSONException{
		String command[]= {"curl","-XGET","192.168.10.43:9200"};
		
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
	    
	    System.out.println(response.toString());
	    JSONObject jsonObject = new JSONObject(response.toString());
	    String tagline = jsonObject.getString("tagline");
	    String name = jsonObject.getString("name");
	    System.out.println(name +  " : " + tagline);
	}**/
	
	// Code for posting data to elastic
	
    private void postElasticInstance(String instance_role, String ip, String status) throws IOException {
		
	    
		//Date now = new Date();
		//SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
	    //SimpleDateFormat DATE_FORMAT_time = new SimpleDateFormat("HH:mm:ss");
	    //DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("ASIA/KOLKATA"));
	    //String date = DATE_FORMAT.format(now)+"T"+DATE_FORMAT_time.format(now)+" +";
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    	String date1 = date.format(new Date());
        System.out.println(date1);
		String command[]= {"curl","-XPOST","192.168.10.43:9200/elk_test2/elk_test2","-H","Content-Type:application/json","-d","{\"post_date\":\""+date1+"\",\"instance_role\":\""+instance_role+"\",\"ip\":\""+ip+"\",\"status\":\""+status+"\"}"};
		ProcessBuilder builder = new ProcessBuilder(command);
	    builder.start();
	}

	
	//Code to get store data from elastic
	
	/** private void getInstanceDetails() throws IOException, JSONException{
	String command[]= {"curl","-XGET","192.168.10.43:9200/_cluster/stats"};
	
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
    
    System.out.println(response.toString());
    //JSONObject jsonObject = new JSONObject(response.toString());
    //String tagline = jsonObject.getString("tagline");
    //String name = jsonObject.getString("name");
    //System.out.println(name +  " : " + tagline);
    
    //System.out.println(jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("user"));
    System.out.println(jsonObject.getJSONObject("hits").getJSONArray("hits").length());
    int length=jsonObject.getJSONObject("hits").getJSONArray("hits").length();
    
   for(int i=0;i<length;i++)
    {
    	String instance_role = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("instance_role");
    	String ip = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("ip");
    	String status = jsonObject.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("status");
    	System.out.println(instance_role+" : "+ip+" : "+status);        	
    	
    }

}**/
}