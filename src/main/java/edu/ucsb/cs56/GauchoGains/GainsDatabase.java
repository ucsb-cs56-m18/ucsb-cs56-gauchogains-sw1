package edu.ucsb.cs56.GauchoGains;

/*
 * Maga Kim and Howard Lin
 * GauchoGains Web App 
 * MongoDB Implementation
 * UCSB CS56 Summer 2018
 * 
 * Some portions:
 * Copyright (c) 2017 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with mongo-3.4.2.jar
 * Documentation: http://api.mongodb.org/java/
 * A Java class connecting to a MongoDB database given a MongoDB Connection URI.
 */

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.List;

import edu.ucsb.cs56.GauchoGains.GainsUser;

public class GainsDatabase {
	private String uriString;
	public GainsDatabase() {
		this.uriString = initMongoDB();
	}
	/**
	  return a HashMap with values of all the environment variables
	  listed; print error message for each missing one, and exit if any
	  of them is not defined.
	  */
	private static HashMap<String,String> getNeededEnvVars(String [] neededEnvVars) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		HashMap<String,String> envVars = new HashMap<String,String>();
		boolean error=false;		
		for (String k:neededEnvVars) {
			String v = processBuilder.environment().get(k);
			if ( v!= null) {
				envVars.put(k,v);
			} else {
				error = true;
				System.err.println("Error: Must define env variable " + k);
			}
		}
		if (error) { System.exit(1); }
		System.out.println("envVars=" + envVars);
		return envVars;	 
	}
	private static String mongoDBUri(HashMap<String,String> envVars) {

		System.out.println("envVars=" + envVars);

		// mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
		String uriString = "mongodb://" +
			envVars.get("MONGODB_USER") + ":" +
			envVars.get("MONGODB_PASS") + "@" +
			envVars.get("MONGODB_HOST") + ":" +
			envVars.get("MONGODB_PORT") + "/" +
			envVars.get("MONGODB_NAME");
		System.out.println("uriString=" + uriString);
		return uriString;
	}

	public String logIn(spark.Request rq) {
		MongoClientURI uri = new MongoClientURI(this.uriString);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());
		MongoCollection<Document> users = db.getCollection("users");

		Document findUser = new Document("_id", rq.queryParams("emailin"));
		if (users.count(findUser) == 0)
			return "No user with that email";
		Document cur = users.find(findUser).first();
		return GainsPassword.checkPassword(rq.queryParams("passwordin"), cur.get("salt").toString(), cur.get("hash").toString()) ? "Login Success" : "Wrong Password";
	}

	public String signUp(spark.Request rq) {
		MongoClientURI uri = new MongoClientURI(this.uriString);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());
		MongoCollection<Document> users = db.getCollection("users");

		String email = rq.queryParams("email").toLowerCase();
		String firstName = rq.queryParams("firstname");
		String lastName = rq.queryParams("lastname");
		String password = rq.queryParams("password");

		GainsUser checkUser = new GainsUser(email, firstName, lastName, password);
		String checkValidUser = checkUser.getValidUserCheck();
		if (checkValidUser.equals("valid"))
			checkValidUser = checkDupeUser(checkUser.getEmail());//, uriString);
		if (checkValidUser.equals("valid")) {
			Document newUser = new Document("_id", checkUser.getEmail())
				.append("firstName", checkUser.getFirstName())
				.append("lastName", checkUser.getLastName())
				.append("salt", checkUser.getPasswordSalt())
				.append("hash", checkUser.getPasswordHash());
			users.insertOne(newUser);
		} else {
			return checkValidUser;
		}	
		return "Sign Up Success";
	}

	//Check if email already exists in database
	public String checkDupeUser(String email) {
		MongoClientURI uri = new MongoClientURI(this.uriString);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(uri.getDatabase());
		MongoCollection<Document> users = db.getCollection("users");

		Document findQuery = new Document("_id", email);
		if(users.count(findQuery) > 0)
			return "Email is already in use";

		return "valid";
	}
	
	//Get MongoDB uri from .env vars
	private static String initMongoDB() {
		HashMap<String, String> envVars = getNeededEnvVars(new String []{
			"MONGODB_USER",
				"MONGODB_PASS",
				"MONGODB_NAME",
				"MONGODB_HOST",
				"MONGODB_PORT"
		});
		return mongoDBUri(envVars);
	}

	private static String makeString(ArrayList<String> text) {
		String resultString = "";
		for (String s: text) {
			resultString += "<b> " + s + "</b><br/>";
		}
		return resultString;
	}

}
