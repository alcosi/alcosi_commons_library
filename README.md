This library is a set of frequently used components and includes (in the future it is planned to split into separate libraries):
- Facilitating synchronization of processes, including those in different threads (com.alcosi.lib.synchronisation)
- Logging of incoming and outgoing requests Http,RabbitMQ (com.alcosi.lib.rabbit,com.alcosi.lib.logging.http,com.alcosi.lib.filters)
- Logging of execution time and errors with annotations (using AspectJ) (com.alcosi.lib.logging.annotations)
- Logging SQL queries/responses and notice/exception (for JDBCTemplate) (com.alcosi.lib.db)
- CORS Filter
- Response caching for incoming Http requests. (com.alcosi.lib.filters)
- Error handling for incoming and outgoing requests Http,RabbitMQ (com.alcosi.lib.rabbit,com.alcosi.lib.logging.http)
- RabbitMQ configuration (com.alcosi.lib.rabbit)
- Facilitating the connection of external JARs to the application. (com.alcosi.lib.utils.ExternalJarLoad)
- Set of serializers for Jackson (com.alcosi.lib.serializers)
- Custom thread pools, including with blocking queue (com.alcosi.lib.executors)
- Swagger and OpenAPI distribution (com.alcosi.lib.doc)
- Load balancer when working with Etherium nodes (com.alcosi.lib.crypto)
- Contract caching for WEB3J (com.alcosi.lib.crypto)
- Automatic registration of frequently used components in Spring (only if available in classpath)

System envs are used as settings


Force to disable components

| Param | Value | Component |
| :---: | :---: | :---: |
| common-lib.smart_contract_creator.disabled | true | Contract caching for WEB3J |
| common-lib.springdoc.disabled | true | Swagger and OpenAPI distribution |
| common-lib.crypto.admins.disabled | true Load balancer when working with Etherium nodes |
| common-lib.filter.all.disabled | true | Filters for all incoming requests (HTTP) |
| common-lib.filter.logging.disabled | true | Logging of incoming requests (HTTP) |
| common-lib.filter.cache.disabled | true | Response caching for incoming Http requests |
| common-lib.filter.cors.disabled | true | CORS Filter |
| common-lib.logging_aspects.disabled | true | Logging of execution time and errors with annotations |
| common-lib.okhttp_logging.disabled | true | Logging of outgoing requests (HTTP,OkHTTP lib) |
| common-lib.rest_template.disabled | true | Logging of outgoing requests (HTTP,RestTemplate)  |
| common-lib.jdbc_template.disabled | true | Logging SQL queries/responses and notice/exception (for JDBCTemplate) |
| common-lib.mapping_helper.disabled | true | Mapping helper |
| common-lib.rabbit.disabled | true | RabbitMQ configuration,  Logging of incoming and outgoing requests (RabbitMQ) |
| common-lib.synchronisation.disabled | true | Facilitating synchronization of processes |

Settings WEB3J

| Param | Format | Description |
| :---: | :---: | :---: |
| common-lib.lifetime.smart_contract_creator | Duration(10m,24h) | Contract caching time for WEB3J |
| common-lib.crypto.address.pk | HEX | Wallet PK for Eherium wallet (For WEB3J contracts) |
| common-lib.crypto.node.{chainId} | RPC Node URIs separated with ", " | URI for Nodes for WEB3J |
| common-lib.request_body_log.max.ok_client_nodes | Int | Max HTTP body size (bytes) to log WEB3J RPC requests/responses |
| common-lib.crypto.node.timeout | Duration(10m,24h) | Http Request timeout for WEB3J |
| common-lib.logging.level.nodes_http | Logging level (JAVA 9) | HTTP requests logging level |


Settings OpenAPI/Swagger

| Param | Format | Description |
| :---: | :---: | :---: |
| common-lib.openapi.url | URI | Path (endpoint) for OpenAPI/Swagger endpoint |
| common-lib.openapi.file-path | Path/URI | Path/URI to OpenAPI file |


Settings Filters

| Param | Format | Description |
| :---: | :---: | :---: |
| common-lib.logging.level.server_logging | Logging level (JAVA 9) | Internal HTTP requests logging level |
| common-lib.server.max.request_body.logging | Int | Internal HTTP requests logging max body size (bytes) |
| common-lib.logging.level.filters.base_order | Int | Base filter order (The order of a particular filter is calculated with respect to this parameter) |
| common-lib.request_body_log.max.ok_client | Int | Max HTTP body size (bytes) to log requests/responses for OkHTTP |
| common-lib.logging.level.ok_http | Logging level (JAVA 9) | OkHTTP HTTP requests logging level |
| common-lib.request_body_log.max.rest_template | Int | Max HTTP body size (bytes) to log requests/responses for RestTemplate |
| common-lib.logging.level.rest_template | Logging level (JAVA 9) | RestTemplate HTTP requests logging level |
| common-lib.request_body_log.max.rabbit | Int | Max RabbitMQ body size (bytes) to log requests/responses |
| common-lib.lifetime.synchronisation_lock | Duration(10m,24h) | Time to force unlock locked thread |
