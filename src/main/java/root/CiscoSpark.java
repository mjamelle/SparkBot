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

import com.ciscospark.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 *
 * @author mjamelle
 */
public class CiscoSpark {
    
    public static final Logger logger = LogManager.getLogger();
    
    final static private String SPARK_API_URL = "https://api.ciscospark.com/v1";
    final static private String DEFAULT_MESSAGE_WEBHOOK = "Maja Webhook";
    final static private String DEFAULT_ROOM_WEBHOOK = "Maja Room Webhook";
    private static Spark ciscospark;
    private static Room sparkroom;
    private static List<Room> mySparkrooms = new ArrayList<Room>() ;
    private static Webhook webhook;
    private static List<Webhook> myWebhooks = new ArrayList<Webhook>() ;    
    private static String accessToken;
    private static String webhookMessageLink;
    private static String webhookRoomsLink;



    public static String getWebhookMessageLink() {
        return webhookMessageLink;
    }

    public static void setWebhookMessageLink(String Dummy) {
        webhookMessageLink = Dummy;
    }
    
    public static String getWebhookRoomsLink() {
        return webhookRoomsLink;
    }
    
    public static void setWebhookRoomsLink(String Dummy) {
        webhookRoomsLink = Dummy;
    }

    public static void ciscoSparkIni (String AccessToken) throws MalformedURLException, URISyntaxException {
        
         // Initialize the Spark client
        ciscospark = Spark.builder()
        .baseUrl(URI.create(SPARK_API_URL))
        .accessToken(AccessToken)
        .build();
        //load rooms into array
        iniRooms ();
        iniWebhooks();
        
        logger.info("iniSpark successful AT : " + AccessToken);
    }
    
    public static void iniRooms ()  {
        
        // List the rooms that I'm in
        ciscospark.rooms()
            .iterate()
            .forEachRemaining(room -> {
                mySparkrooms.add(room);
                logger.info("iniRoom : "+room.getTitle());
            });        
        
    };
    
    public static void addRoom (String id)  { 
        // add room
        Room receive = new Room();
        receive = ciscospark.rooms().path("/"+id, Room.class).get();
        mySparkrooms.add(receive);
        logger.info("addRoom : " + receive.getTitle());    
    };
    
    public static int getRoomamount ()  {  
        return mySparkrooms.size();
    };   
        
    public static void iniWebhooks () throws MalformedURLException, URISyntaxException {
        
        // List and initialize the Webhooks  Bot is in
        ciscospark.webhooks()
            .iterate()
            .forEachRemaining(webhook -> {
                myWebhooks.add(webhook);
                logger.info("iniWebhook : " + webhook.getTargetUrl().toString());
            });  
    
        //check if  Default Message Webhook exist 
        boolean webhookexist = false;
        for(Webhook intern : myWebhooks)  {
            if (webhookMessageLink.equals(intern.getTargetUrl().toString())) webhookexist = true;
        }
        //if not then add Default Message Webhook   
            if (!webhookexist) {
                Webhook webhook = new Webhook();
                webhook.setName(DEFAULT_MESSAGE_WEBHOOK);

                URL url = new URL(webhookMessageLink);
                URI uri = url.toURI();

                webhook.setTargetUrl(uri);
                webhook.setResource("messages");
                webhook.setEvent("created");
                myWebhooks.add(webhook);
                ciscospark.webhooks().post(webhook);    
            }
        //check if  Default Rooms Webhook exist 
        webhookexist = false;
        for(Webhook intern : myWebhooks)  {
            if (webhookRoomsLink.equals(intern.getTargetUrl().toString())) webhookexist = true;
        }
        //if not then add Default Rooms Webhook   
            if (!webhookexist) {
                Webhook webhook = new Webhook();
                webhook.setName(DEFAULT_MESSAGE_WEBHOOK);

                URL url = new URL(webhookRoomsLink);
                URI uri = url.toURI();

                webhook.setTargetUrl(uri);
                webhook.setResource("rooms");
                webhook.setEvent("created");
                myWebhooks.add(webhook);
                ciscospark.webhooks().post(webhook);    
            }
    }; 
    
    public static int getWebhookscounter ()  {  
        return myWebhooks.size();
    };  
    
       
    public static Message getMessage(String id) {
        Message receive = new Message();
        receive = ciscospark.messages().path("/"+ id, Message.class).get();
    return (receive);
    }
    
    public static void sendMessage (Message message) {
        logger.info("sendMessage : " + message.getText());
        ciscospark.messages().post(message);
    }
    
}