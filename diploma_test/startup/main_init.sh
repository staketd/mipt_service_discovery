sudo apt-get -y update
sudo apt-get install ifupdown openjdk-17-jdk -y

git clone https://github.com/staketd/mipt_service_discovery
git clone https://github.com/iskander232/Controller


wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz
sudo mkdir -p /usr/local/apache-maven
sudo tar -xvf /home/vagrant/apache-maven-3.8.5-bin.tar.gz -C /usr/local/apache-maven
export PATH=/usr/local/apache-maven/apache-maven-3.8.5/bin:$PATH
echo "export PATH=/usr/local/apache-maven/apache-maven-3.8.5/bin:$PATH" >> /home/vagrant/.bashrc

wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list
sudo apt-get update -y
sudo apt-get install -y mongodb-org=5.0.8 mongodb-org-database=5.0.8 mongodb-org-server=5.0.8 mongodb-org-shell=5.0.8 mongodb-org-mongos=5.0.8 mongodb-org-tools=5.0.8

printf "replication:\n  oplogSizeMB: 2000\n  replSetName: rs0\n" | sudo tee -a /etc/mongod.conf

sudo systemctl start mongod
mongo --eval "rs.initiate()"

cd Controller
mvn wrapper:wrapper
./mvnw package
cd ..
cd mipt_service_discovery
mvn wrapper:wrapper
./mvnw package
cd ..

java -jar Controller/target/controller-0.0.1-SNAPSHOT.jar &
java -jar mipt_service_discovery/service-discovery/target/service-discovery-1.0.jar &

