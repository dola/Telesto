#!/bin/sh
#
#


serverMachine=""
clientMachine=""
remoteUserName=""
experimentId=""

# clientTypes and Numbers
configFile="config.properties"
. $configFile

# values: 	$cliOneWayCount
# 			$cliRequestResponseCount

runTime=120


#####################################
#
# Copy server and clients to machines
#
#####################################

echo -ne "  Testing passwordless connection to the server machine and client machine... "
# Check if command can be run on server and client
success=$( ssh -o BatchMode=yes  $remoteUserName@$serverMachine echo ok 2>&1 )
if [ $success != "ok" ]
then
	echo "Passwordless login not successful for $remoteUserName on $serverMachine. Exiting..."
	exit -1
fi

success=$( ssh -o BatchMode=yes  $remoteUserName@$clientMachine echo ok 2>&1 )
if [ $success != "ok" ]
then
	echo "Passwordless login not successful for $remoteUserName on $clientMachine. Exiting..."
	exit -1
fi
echo "OK"

echo "  Copying server.jar to server machine: $serverMachine ... "
# Copy jar to server machine
scp Telesto.jar $remoteUserName@$serverMachine:~/
echo "  Copying client.jar to client machine: $serverMachine ... "
# Copy jar to client machine
scp Telesto.jar $remoteUserName@$clientMachine:~/

######################################
#
# Run server and clients
#
######################################

# Run server
echo "  Starting the server on $serverMachine"
ssh $remoteUserName@$serverMachine "java -jar Telesto.jar MW" &

# Wait for the server to start up
sleep 5

echo "  Start the $cliOneWayCount OneWay clients on the client machine: $clientMachine"
# Run the clients
clientIds=`seq $cliOneWayCount`
for clientId in $clientIds
do
	echo "    Start OneWay client: $clientId"
	ssh $remoteUserName@$clientMachine "java -jar Telesto.jar CL id $clientId test ONE_WAY" &
done

clientIds=`seq 1 2 $cliRequestResponseCount`
for i in $clientIds
do
	echo "    Start pairClient client: $i"
	ssh $remoteUserName@$clientMachine "java -jar Telesto.jar CL id $(($i+$cliOneWayCount)) test REQUEST_RESPONSE_PAIR_CLIENT" &
	echo "    Start pairServer client: $(($i+1))"
	ssh $remoteUserName@$clientMachine "java -jar Telesto.jar CL id $(($i+$cliOneWayCount+1)) test REQUEST_RESPONSE_PAIR_SERVER" &
done

clientIds=`seq 1 2 $cliRequestResponseCount`
for i in $clientIds
do
	echo "    Start REQUEST_SERVICE client: $i"
	ssh $remoteUserName@$clientMachine "java -jar Telesto.jar CL id $(($i+$cliOneWayCount+$cliRequestResponseCount)) test REQUEST_SERVICE" &
	echo "    Start SERVE_SERVICE client: $(($i+1))"
	ssh $remoteUserName@$clientMachine "java -jar Telesto.jar CL id $(($i+$cliOneWayCount+$cliRequestResponseCount+1)) test SERVE_SERVICE" &
done

# wait for runTime to go over
sleep $runTime

echo "  Sending shut down signal to clients"
ssh $remoteUserName@$clientMachine "killall java"

echo "  Sending shut down signal to server"
# Send a shut down signal to the server
ssh $remoteUserName@$serverMachine "killall java"

# wait for shutdown to complete
sleep 10

########################################
#
# Copy and process logs and plot graphs
#
########################################

# Copy log files from the clients
mkdir -p $experimentId/clients
echo "  Copying log files from client machine... "
scp -r $remoteUserName@$clientMachine:~/log ./$experimentId/clients

mkdir -p $experimentId/middleware
echo "  Copying log files from client machine... "
scp -r $remoteUserName@$serverMachine:~/log ./$experimentId/middleware

# Cleanup
#echo -ne "  Cleaning up files on client and server machines... "
#ssh $remoteUserName@$clientMachine "rm -rf ~/log"
#ssh $remoteUserName@$serverMachine "rm -rf ~/log"
#echo "OK"

# Process the log files from the clients
echo "  Processing log files"
cat $experimentId/clients/* | sort -n > $experimentId/allclients.log

