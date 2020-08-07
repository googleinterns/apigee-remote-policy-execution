# Apigee Remote Policy Execution 
The purpose of this project is to provide a proof of concept and protocol for
running Apigee policies on remote HTTP servers. The `execute.proto` file in
`proto` defines a protocol buffer schema for encoding the information
required by Apigee policies. 

The Java callout in `callout` encodes and serializes the Execution interface and
sends it over the wire to a remote server (in this case, a Cloud Functions
endpoint) for execution. 

The Cloud Functions code is located within `remote-policy`  and deserializes the
protocol buffer message before performing some operations on it and ultimately
returning the message to the caller.

**This is not an officially supported Google product.**