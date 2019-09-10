# GraphDBLP backend
The backend expose API to perform autocomplete feature in the UI. It consists in a Spring application that runs into Docker.
The backend uses a mongoDB instance, provided as a docker; the application itself load automatically the data into the database.

# Run the container

The repository contains both the source code and the binaries; the latter are used to build the docker image.
Create and run the mongoDB container, then the graphdblp-backend container:

	sudo docker run --name mongodb -p 27018:27017  -d mongo:3.6
	
	sudo docker run -it --name graphdblp-backend-container -p 8081:8081 --link=mongodb -d andreascrivanti/graphdblp-backend

# Compile and deploy on your machine
If you want to modify the source code and deploy it on your machine, you need:

Requirements:
1. UNIX system
1. Docker installed on your machine
1. OpenJDK installed on your machine
1. Spring Tool Suite (or Eclipse)

Clone this repository, import the project in Spring Tool Suite, and modify the code.
Then go into `scripts` and launch `deploy.sh`; it compiles source code and builds the docker image, using docker on your machine.

Now remove (if already exists), create and launch the container:

	sudo docker rm -f graphdblp-backend-container
	sudo docker run -it --name graphdblp-backend-container -p 8081:8081 --link=mongodb -d graphdblp-backend
	
The application will be upper running in about 5 minutes.
You can check the application log using the command:

	sudo docker logs graphdblp-backend-container
	
Now the API should be running; test on browser the link [http://localhost:8081/graphdblp/meta/](http://localhost:8081/graphdblp/meta/)
