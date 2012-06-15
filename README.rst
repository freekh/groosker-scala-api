Groosker Scala API
==================

Welcome to Grooskers Scala API repo. 

More information will come shortly, but feel free to have a look at the source code and our `groosker.com<http://groosker.com>`_...

java -Duser.dir=/home/hudson/.hudson/jobs/Groosker/workspace -Dsbt.log.noformat=true -Dbuild.number=$BUILD_NUMBER -DGIT_BRANCH=master -DJOB_NAME=$JOB_NAME -jar /home/hudson/sbt-launch-0.11.jar clean update package
java -Duser.dir=/home/hudson/.hudson/jobs/GrooskerScalaAPI/  -Dsbt.log.noformat=true -Dbuild.number=$BUILD_NUMBER -DGIT_BRANCH=master -DJOB_NAME=$JOB_NAME -jar /home/hudson/sbt-launch-0.11.jar clean update

java -Duser.dir=/home/hudson/.hudson/jobs/Groosker Scala API/workspace -Dsbt.log.noformat=true -Dbuild.number=$BUILD_NUMBER -DGIT_BRANCH=master -DJOB_NAME=$JOB_NAME  -Dpublish.dir=/usr/local/nginx/html/maven -jar /home/hudson/sbt-launch-0.11.jar clean update publish