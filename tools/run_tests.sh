#!/bin/bash -e
#
#

experimentId=$1

while [ -e "config$experimentId.properties" ]; do
    mv "config$experimentId.properties" config.properties
    echo
    echo
    echo Running Experiment $experimentId
    echo
    ./run_test.sh $experimentId
    experimentId=$[experimentId + 1]
done