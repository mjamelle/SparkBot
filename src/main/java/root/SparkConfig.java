/*
 * Copyright (C) 2017 mjamelle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package root;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author mjamelle
 */
public class SparkConfig {
    
    private static final Logger logger = LogManager.getLogger();
    
    private String mConfigFile = "config/config.json";
    private final String webhookMessageroute = "webhook/messages";
    private final String webhookRoomsroute = "webhook/rooms";
    public static final String BOTNAME = "maja@sparkbot.io";
    public static final String WEBFILELOCATION = "/web";
    public static final String LAYOUT = "web/templates/layout.vtl";
    public static final int PORT = 4567;
    
    private String AccessToken;
    private String WebhookURL;
    private String ServerURL;
    private String ServerPort;
    
    
    SparkConfig() {
       loadconfig();
    }
    
    SparkConfig(String mFilename) {
       this.mConfigFile = mFilename; 
       loadconfig();
    }

    public void setAccessToken(String AccessToken) {
        this.AccessToken = AccessToken;
    }

    
    public void setServerURL(String ServerURL) {
        this.ServerURL = ServerURL;
    }    

    public void setServerPort(String ServerPort) {
        this.ServerPort = ServerPort;
    }     
    
    public String getAccessToken() {
        return this.AccessToken;
    }

    public String getServerURL() {
        return this.ServerURL;
    }

    public String getServerPort() {
        return this.ServerPort;
    }      
    
    public String getWebhookMessageLink() {
        this.WebhookURL = "http://" + ServerURL + ":" + ServerPort + "/" + webhookMessageroute;
        return this.WebhookURL;
    }

    public String getWebhookRoomsLink() {
        this.WebhookURL = "http://" + ServerURL + ":" + ServerPort + "/" + webhookRoomsroute;
        return this.WebhookURL;
    }    
 
    private void loadconfig ()  {
        try {
        JSONParser parser = new JSONParser();
        FileReader config = new FileReader(mConfigFile);
           
        Object obj = parser.parse(config);
        JSONObject jsonObject =  (JSONObject) obj;
        this.AccessToken = (String) jsonObject.getOrDefault("AccessToken","");
        this.ServerURL = (String) jsonObject.getOrDefault("ServerURL", "www.example.com");
        this.ServerPort = (String) jsonObject.getOrDefault("ServerPort","8080");
        logger.info("loadconfig successful");
        
        } catch (FileNotFoundException e) {
            logger.log(Level.ERROR,"Please check if config file  /config/config.json exist",e);
        } catch (IOException e) {
            logger.log(Level.ERROR,e);
        } catch (ParseException e) {
            logger.log(Level.ERROR,"Please check if config file has the right json format",e);
        }   
    }      
   
    public void writeconfig () { 
        
        //load config into JSONObject
        JSONObject obj = new JSONObject();
        obj.put("Name", "Sparkbot");
        obj.put("AccessToken", AccessToken);
	obj.put("ServerURL", ServerURL);
        obj.put("ServerPort", ServerPort);    
        // save into file 
        try {
        FileWriter configFile = new FileWriter(mConfigFile);
        configFile.write(obj.toJSONString());
        configFile.flush();
        configFile.close();    
        
        } catch (Exception e) {
            logger.log(Level.ERROR,"could'nt write config file ",e);
        }      
            logger.info("Write Config File");
            logger.debug("Write Config File : " + obj);
    }
    
}
