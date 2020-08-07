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

## Source Code Headers

Every file containing source code must include copyright and license
information. This includes any JS/CSS files that you might be serving out to
browsers. (This is to help well-intentioned people avoid accidental copying that
doesn't comply with the license.)

Apache header:

    Copyright 2020 Google LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
