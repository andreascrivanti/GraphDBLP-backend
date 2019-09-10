#this script must be launched from the parent directory of 'scripts' directory, where the Dockerfile is located
sudo ./mvnw clean
sudo ./mvnw package
sudo docker rmi -f graphdblp-backend
sudo docker build -t "graphdblp-backend" .
sudo docker save -o target/graphdblp-backend.tar graphdblp-backend:latest

