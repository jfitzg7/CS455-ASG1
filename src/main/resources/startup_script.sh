#!/bin/bash

jar_path=CS455/ASG1/build/libs/ASG1-1.0.jar
user=jhfitzg
domain=cs.colostate.edu
registry_host=129.82.44.157
registry_port=5001
machine_list=./machine_list

for (( c=1; c<=1; c++))
do
  for machine in `cat $machine_list`
  do
    gnome-terminal -- bash -c "ssh -t $user@$machine.$domain 'java -cp $jar_path cs455.overlay.node.MessagingNode $registry_host $registry_port'"
  done
done