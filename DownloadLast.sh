#!/bin/bash
#This script download the last stable build on jenkins

data=$(curl -g "https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/lastStableBuild/api/xml?xpath=/freeStyleBuild/artifact&wrapper=artifacts")
relativePath=$(grep -oPm1 "(?<=<relativePath>)[^<]+" <<< "$data")
jarFile=$(grep -oPm1 "(?<=<fileName>)[^<]+" <<< "$data")

if [[ $BRANCH_NAME=="master"  ]]; then
	url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/lastStableBuild/artifact/"${relativePath}
else
	url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle%20Devel/lastStableBuild/artifact/"${relativePath}
fi
echo ${url}

wget ${url} -O bot.jar


