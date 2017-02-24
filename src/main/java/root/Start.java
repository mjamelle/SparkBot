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


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import static spark.Spark.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import spark.ModelAndView;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;


        

/**
 *
 * @author mjamelle
 */
public class Start {
    
    //public static final Logger logger = LogManager.getLogger();
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException {

        //setup logging infrastructure
        System.setProperty("log4j.configurationFile","config/log4j2.xml");
        Logger logger = LogManager.getLogger();
        //Configurator.setLevel("root.Start", Level.DEBUG);
        
        //start Spark web server and set defaults
        staticFileLocation(SparkConfig.WEBFILELOCATION);
        String layout = SparkConfig.LAYOUT;
        port(SparkConfig.PORT);
        
        //general inits
        BotLogic.initranslate();
        SparkConfig config = new SparkConfig ();  //config file ini
        CiscoSpark.setWebhookMessageLink(config.getWebhookMessageLink());
        CiscoSpark.setWebhookRoomsLink(config.getWebhookRoomsLink());       
        CiscoSpark.ciscoSparkIni(config.getAccessToken()); // Spark Object ini and access code from config file    
        logger.info("Spark initilized");

        
        //web routes responses---------------------------------------------------
        get("/", (request, response) -> {
            logger.info("/ Web request");
            logger.debug("/ Web request : " + request.body());
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("template", "/web/Welcome.html" );
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        get("/setup", (request, response) -> {
            logger.info("/setup Web request");
            logger.debug("/setup Web request : " + request.body());
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("template", "/web/Setup_form.html");
            model.put("AccessToken", config.getAccessToken());
            model.put("ServerURL", config.getServerURL());
            model.put("ServerPort", config.getServerPort());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
 
        get("/setup_submit", (request, response) -> {
            logger.info("/setup_submit Web request");
            logger.debug("/setup_submit Web request : " + request.body());
            config.setAccessToken(request.queryParams("AccessToken"));
            config.setServerURL(request.queryParams("ServerURL"));
            config.setServerPort(request.queryParams("ServerPort")); 
            config.writeconfig();
            response.redirect("/");
            return "ok";
        });

        get("/info", (request, response) -> {
            logger.info("/info Web request");
            logger.debug("/info Web request : " + request.body());
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("template", "/web/Info_form.html");
            model.put("Botrequestcounter", BotLogic.getBotrequestcounter());
            model.put("Roomamount", CiscoSpark.getRoomamount());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
              
        post("webhook/messages", (request, response) -> {
            logger.info("/webhook/messages Web request");
            logger.debug("/webhook/messages Web request : " + request.body());
            BotLogic.webHookMessageTrigger(request);
            return "ok";  
        });
               
        post("/webhook/rooms", (request, response) -> {
            logger.info("/webhook/rooms Web request");
            logger.debug("/webhook/rooms Web request : " + request.body());
            BotLogic.webHookRoomsTrigger(request);
            return "ok";  
        });        
    }
    
}

