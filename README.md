# MongoGains

Credit: The [original version of this tutorial](https://github.com/Figler/ucsb-cs56-mongodb-mlab-tut) is by [Chandler Forrest](https://github.com/figler), from Summer 2018).

# Usage
Edit pom.xml as necessary for correct heroku deployment.
Create .env to
```
MONGODB_USER=dbuser
MONGODB_PASS=dbpassword
MONGODB_NAME=dbname
MONGODB_HOST=dbhosturl
MONGODB_PORT=dbport
```

# Done
* Implemented code from firebasegains repo to interact correctly with MongoDB.
* Wrote to MongoDB on sign up and read on log in.
* Hashed passwords via PBKDF2 SHA1.

# References

* Some information about Databases MongoDB from a summer Program Prof. Conrad and Prof. Diba Miza helped design for UC San Diego:
    * <https://ucsd-cse-spis-2016.github.io/webapps/databases_mongodb/>
    * <https://ucsd-cse-spis-2016.github.io/webapps/databases/>
* MongoDB Installation: <https://docs.mongodb.com/manual/installation/>
* MongoDB Examples: <https://blog.mlab.com/2011/11/ample-mongodb-examples/>
