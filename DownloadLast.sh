#!/bin/bash
#This script download the last stable build on jenkins
echo "Branch: "$1
if [[  $1 = "master"  ]]
then
	base_url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/lastStableBuild"
else
	base_url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle%20Devel/lastStableBuild/"
fi

data=$(curl -s -g ${base_url}"/api/xml?xpath=/freeStyleBuild/artifact&wrapper=artifacts")
relativePath=$(grep -oPm1 "(?<=<relativePath>)[^<]+" <<< "$data")
jarFile=$(grep -oPm1 "(?<=<fileName>)[^<]+" <<< "$data")



wget ${base_url}"/artifact/"${relativePath} -O bot.jar -nq


