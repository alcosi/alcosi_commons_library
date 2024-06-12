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
- Interface and wrappers for encryption/decryption (com.alcosi.lib.secured.encrypt)
- Encryption key provider interface and implementations - in env. variable and through http
- Thread context form headers/to headers (com.alcosi.lib.filters,com.alcosi.lib.logging.http)
- Simple authentication (com.alcosi.lib.filters)
- Secured data containers with JSON serialization and log masking (com.alcosi.lib.secured.container,com.alcosi.lib.secured.logging.files,com.alcosi.lib.serializers)

System envs are used as settings


Force to disable components

|                       Param                       |                        Value                        |                                   Component                                   |
|:-------------------------------------------------:|:---------------------------------------------------:|:-----------------------------------------------------------------------------:|
| common-lib.crypto.smart-contract-creator.disabled |                        true                         |                          Contract caching for WEB3J                           |
|            common-lib.openapi.disabled            |                        true                         |                       Swagger and OpenAPI distribution                        |
|          common-lib.crypto.node.disabled          | true Load balancer when working with Etherium nodes |
|          common-lib.filter.all.disabled           |                        true                         |                   Filters for all incoming requests (HTTP)                    |
|        common-lib.filter.logging.disabled         |                        true                         |                      Logging of incoming requests (HTTP)                      |
|         common-lib.filter.cache.disabled          |                        true                         |                  Response caching for incoming Http requests                  |
|          common-lib.filter.cors.disabled          |                        true                         |                                  Cors filter                                  |
|        common-lib.filter.context.disabled         |                        true                         |                         Thread context filter Filter                          |
|          common-lib.filter.auth.disabled          |                        true                         |                           Simple auth filter Filter                           |
|        common-lib.logging_aspects.disabled        |                        true                         |             Logging of execution time and errors with annotations             |
|            common-lib.okhttp.disabled             |                        true                         |                Logging of outgoing requests (HTTP,OkHTTP lib)                 |
|         common-lib.rest-template.disabled         |                        true                         |               Logging of outgoing requests (HTTP,RestTemplate)                |
|         common-lib.jdbc-template.disabled         |                        true                         |     Logging SQL queries/responses and notice/exception (for JDBCTemplate)     |
|        common-lib.mapping-helper.disabled         |                        true                         |                                Mapping helper                                 |
|            common-lib.rabbit.disabled             |                        true                         | RabbitMQ configuration,  Logging of incoming and outgoing requests (RabbitMQ) |
|        common-lib.synchronisation.disabled        |                        true                         |                   Facilitating synchronization of processes                   |
|            common-lib.secured.disabled            |                        true                         |                             Encryption components                             |

All other settings should be visible  through IDEA spring-configuration-metadata.json helper

### To add library to gradle project:

````kotlin
dependencies {
//Other dependencies
    implementation("com.alcosi:commons-library-basic-dependency:3.3.0.4.0.5")
//Other dependencies
}