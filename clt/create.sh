#!/bin/bash

openssl req -new -x509 -days 9999 -config cnf/ca.cnf -keyout ca-key.pem -out ca-crt.pem

openssl genrsa -out server-key.pem 4096
openssl req -new -config cnf/server.cnf -key server-key.pem -out server-csr.pem
openssl x509 -req -extfile cnf/server.cnf -days 999 -passin "pass:password" -in server-csr.pem -CA ca-crt.pem -CAkey ca-key.pem -CAcreateserial -out server-crt.pem  

openssl genrsa -out client-key.pem 4096
openssl req -new -config cnf/client.cnf -key client-key.pem -out client-csr.pem
openssl x509 -req -extfile cnf/client.cnf -days 999 -passin "pass:password" -in client-csr.pem -CA ca-crt.pem -CAkey ca-key.pem -CAcreateserial -out client-crt.pem

