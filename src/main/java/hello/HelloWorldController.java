package hello;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;



@Controller
@RequestMapping("/webhook")
public class HelloWorldController {

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody WebhookResponse webhook(@RequestBody String obj){
     	String speech = null;
try{
	    	JSONObject objS = new JSONObject(obj);
//          System.out.println(objS.get("id"));
          JSONObject result = objS.getJSONObject("result");
          String action = result.getString("action");
          System.out.println("action ::: " + action);
          JSONObject parameter = result.getJSONObject("parameters");
          String geoCity = parameter.getString("geo-city");
          System.out.println("geoCity ::: " + geoCity);
          if(!geoCity.equals("")){
            System.out.println("geoCity ::: " + geoCity);
            }else{
                System.out.println("else");
                geoCity = parameter.getJSONObject("address").getString("city");
                System.out.println("geoCity elsde ::: " + geoCity);
            } 
         
            
           String Service_url = "http://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+geoCity+"\")&format=json";
           System.out.println("url before   "+Service_url);
           
           Service_url = Service_url.replace(" ", "%20");
           Service_url = Service_url.replace("\"", "%22");
           
          if(action.equals("yahooWeatherForecast")&&geoCity!=""){
        	  
        	  System.out.println(" inside if");
              
        	  
        	  try {

        			URL url = new URL(Service_url);
        			 System.out.println(" 1");
        			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        			conn.setRequestMethod("GET");
        			//conn.setRequestProperty("Accept", "application/json");
        			System.out.println(conn.getResponseCode());
        			if (conn.getResponseCode() != 200) {
        				
        				speech = "code "+conn.getResponseCode();
           			
        			}
        			
       			 
        			// Created a BufferedReader to read the contents of the request.
        	        BufferedReader in = new BufferedReader(
        	                new InputStreamReader(conn.getInputStream()));
        	        String inputLine;
        	        StringBuilder response = new StringBuilder();

        	        while ((inputLine = in.readLine()) != null) {
        	            response.append(inputLine);
        	        }

        	        // MAKE SURE TO CLOSE YOUR CONNECTION!
        	        in.close();

        	        // response is the contents of the XML
        	        System.out.println(response.toString());
        	    
        			
        			JSONObject respoobj = new JSONObject(response.toString());
        			
                  JSONObject query = respoobj.getJSONObject("query");
                  JSONObject results = query.getJSONObject("results");
                  JSONObject channel = results.getJSONObject("channel");
                  JSONObject item = channel.getJSONObject("item");
                  JSONObject condition = item.getJSONObject("condition");
                  String temp=condition.getString("temp");
                  String text=condition.getString("text");
                  
                  JSONObject location = channel.getJSONObject("location");
                  String city=location.getString("city");

                  JSONObject units = channel.getJSONObject("units");
                  String temperature=units.getString("temperature");


        			

    				  speech = "Today in " +city + ": "+text+", the temperature is " +temp + " "+temperature;
 
    				 System.out.println(speech);
        			conn.disconnect();

        		  }catch (IOException e) {

        			  

        		  }  
        	  
        	  
        	  
         
          }
}catch(Exception e){
	//speech = "last ctach"+e.getMessage();
}


	        return new WebhookResponse( speech, speech);

       // return new WebhookResponse("Hello! " + obj, "Text " + obj);
    }
}
