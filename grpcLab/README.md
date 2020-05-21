
about the lab:

this lab aims to improve my knowledge about gRPC and protocol buffer.<br>

There are 3 remote procedure provided by the server : <br>

simpleSum -> that compute the sum of two integers.<br>
repeatedSum -> which compute a simple operation and send a stream of messages from the server to the client.<br>
streamSum -> which just build a bi-directional stream. The client may send multiple request to the server<br>
and the server will send back a response for each request. The stream will be closed once the client's quit signal will be sent.<br>

how to use:<br>

Install maven (protoc compiler will be used inside maven build-procedure)<br>
import  as maven project<br>
download dependencies<br>
build with maven-compile <br>
