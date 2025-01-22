# Multithreaded HTTP Server

This project is an implementation of HTTP server in Java. It is a training project, the purpose of which is to demonstrate knowledge about the principles of the HTTP protocol, the implementation of parsing and processing of requests, and the formation of responses. The server is designed with simplicity and extensibility in mind and is intended for further development.

## Key features
1. Handles multiple client connections concurrently using separate threads for each connection.
2. Manages server configuration through a JSON file.
3. Uses server and client sockets to handle network communication.

## Dependencies
1. Logs server activities and errors using SLF4J.
2. Json parsing via Jackson

## Note
The webroot is specified in the server configuration file and is used by the server to locate and serve the requested files.
