
about the lab:

this lab aim to improve my knowledge abou gRPC and protocol buffer.
There are 3 remote procedure provided by the server :

simpleSum -> that compute the sum of two integers
repeatedSum -> which compute a simple product and send a stream of messages from server to client
streamSum -> which just build a bi-directional stream. The client may send multiple request to the server
and the server will send back a response for each request. The stream will be closed once the client's quit signal will be sent.

how to use:

Install maven (protoc compiler will be used inside maven build-procedure)
import  as maven project
download dependencies
build with maven-compile 
