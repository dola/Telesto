#!/bin/bash -e
#
#


serverMachine="ec2-54-194-10-35.eu-west-1.compute.amazonaws.com"
clientMachine="ec2-54-194-38-109.eu-west-1.compute.amazonaws.com"
remoteUserName="ubuntu"
loggingArgs="-Djava.util.logging.config.file=/home/ubuntu/logging.properties -Djava.util.logging.manager=ch.ethz.syslab.telesto.common.util.ShutdownLogManager"

# clientTypes and Numbers
configFile="config.properties"
. $configFile

experimentId=$1

# values: 	$cliOneWayCount
# 			$cliRequestResponseCount


#####################################
#
# Copy server and clients to machines
#
#####################################

echo "  Copying server.jar to server machine: $serverMachine ... "
# Copy jar to server machine
scp Telesto.jar $remoteUserName@$serverMachine:~/
scp config.properties $remoteUserName@$serverMachine:~/
scp logging.properties $remoteUserName@$serverMachine:~/
echo "  Copying client.jar to client machine: $serverMachine ... "
# Copy jar to client machine
scp Telesto.jar $remoteUserName@$clientMachine:~/
scp config.properties $remoteUserName@$clientMachine:~/
scp logging.properties $remoteUserName@$clientMachine:~/

echo "  Initializing database ... "
export PGPASSWORD=$dbPassword
psql -q -h $dbServerName -U $dbUser -p $dbPortNumber < cleanup.sql
psql -q -h $dbServerName -U $dbUser -p $dbPortNumber < init.sql

######################################
#
# Run server and clients
#
######################################

# Run server
echo "  Starting the server on $serverMachine"
ssh $remoteUserName@$serverMachine "java $loggingArgs -jar Telesto.jar MW" &

# Wait for the server to start up
sleep 5

echo "  Start the $cliOneWayCount OneWay clients on the client machine: $clientMachine"
# Run the clients
clientIds=`seq $cliOneWayCount`
for clientId in $clientIds
do
echo "    Start OneWay client: $clientId"
ssh $remoteUserName@$clientMachine "java $loggingArgs -jar Telesto.jar CL id $clientId test ONE_WAY" &
sleep 0.3
done

sleep 1

clientIds=`seq 1 2 $cliRequestResponseCount`
for i in $clientIds
do
echo "    Start pairClient client: $(($i+$cliOneWayCount))"
ssh $remoteUserName@$clientMachine "java $loggingArgs -jar Telesto.jar CL id $(($i+$cliOneWayCount)) test REQUEST_RESPONSE_PAIR_CLIENT" &
sleep 0.3
echo "    Start pairServer client: $(($i+$cliOneWayCount+1))"
ssh $remoteUserName@$clientMachine "java $loggingArgs -jar Telesto.jar CL id $(($i+$cliOneWayCount+1)) test REQUEST_RESPONSE_PAIR_SERVER" &
sleep 0.3
done

sleep 1

clientIds=`seq 1 2 $cliRequestResponseCount`
for i in $clientIds
do
echo "    Start REQUEST_SERVICE client: $(($i+$cliOneWayCount+$cliRequestResponseCount))"
ssh $remoteUserName@$clientMachine "java $loggingArgs -jar Telesto.jar CL id $(($i+$cliOneWayCount+$cliRequestResponseCount)) test REQUEST_SERVICE" &
sleep 0.3
echo "    Start SERVE_SERVICE client: $(($i+$cliOneWayCount+$cliRequestResponseCount+1))"
ssh $remoteUserName@$clientMachine "java $loggingArgs -jar Telesto.jar CL id $(($i+$cliOneWayCount+$cliRequestResponseCount+1)) test SERVE_SERVICE" &
sleep 0.3
done

# wait for runTime to go over
sleep $runTime

echo "  Sending shut down signal to clients"
ssh $remoteUserName@$clientMachine "killall java"

echo "  Sending shut down signal to server"
# Send a shut down signal to the server
ssh $remoteUserName@$serverMachine "killall java"

# wait for shutdown to complete
sleep 30

########################################
#
# Copy and process logs and plot graphs
#
########################################

# Copy log files from the clients
mkdir -p $experimentId/clients
echo "  Copying log files from client machine... "
scp -r $remoteUserName@$clientMachine:~/log/* ./$experimentId/clients

mkdir -p $experimentId/middleware
echo "  Copying log files from client machine... "
scp -r $remoteUserName@$serverMachine:~/log/* ./$experimentId/middleware

cp config.properties $experimentId/

# Cleanup
echo -ne "  Cleaning up files on client and server machines... "
ssh $remoteUserName@$clientMachine "rm -rf ~/log"
ssh $remoteUserName@$serverMachine "rm -rf ~/log"
echo "OK"

# Process the log files from the clients
echo "  Processing log files"
cat $experimentId/clients/* | sort -n > $experimentId/allclients.log

