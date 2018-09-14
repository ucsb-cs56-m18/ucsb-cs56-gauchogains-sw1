/*
 * Maga Kim and Howard Lin
 * GauchoGains Web App
 * MongoDB Implementation
 * UCSB CS56 Summer 2018 
 */  
package edu.ucsb.cs56.GauchoGains;

import edu.ucsb.cs56.GauchoGains.GainsDatabase;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class MongoGains {

	public static void main(String[] args) {
		//Instantiate database which contains MongoDB uri
		GainsDatabase db = new GainsDatabase();

		//Link to Heroku port
		port(getHerokuAssignedPort());

		Map map = new HashMap();
		get("/", (rq, rs) -> new ModelAndView(map, "signupform.mustache"), new MustacheTemplateEngine());
		
		//On Sign Up button click take form fields to sign up through database
		post("/signup", (rq,rs) -> db.signUp(rq));

		//On Log In button click take form fields to login through database
		post("/login", (rq,rs) -> db.logIn(rq));

	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
	}

}
