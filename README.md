openstack
=========

A TOSCA NodeType and ImplementationArtifact for interacting with an OpenStack IaaS

input
=========
all web methods require at least the two inputs:
* credentials
* endpointsAPI

their syntax must be:
* credentials: {"auth":{"tenantId":"???","passwordCredentials":{"username":"???","password":"???"}}}
* endpointsAPI: {"os-identity-api":"http://129.69.209.127:5000/v2.0","os-tenantId":"???"}

the other inputs are either OpenStack uuids or entity names...
