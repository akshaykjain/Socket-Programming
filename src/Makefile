all:
	javac -cp .:../lib/* MasterBot.java
	javac -cp .:../lib/* SlaveBot.java

run_master:
	java -cp .:../lib/* MasterBot -p 2317

run_slave:
	java -cp .:../lib/* SlaveBot -h localhost -p 2317

clear:
	rm -rf *.class
