# play-sockjs-akka-clustering
A quick example project to show how to allow external services to talk to websockets created in play framework.

Start up in the root directory type

    sbt run

With your browser navigate to http://localhost:9000

Press connect and then type any messages you should see them print out on the browser screen.

Open another window and then navigate to http://localhost:9000/post?=postValue=1

Open the other window where you connected to the server and you will the value of "postValue" printed. 
You can change this value in the URL.


export HOST=$( ifconfig | grep "inet " | grep -Fv 127.0.0.1 | awk '{print $2}' )