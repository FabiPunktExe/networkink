# networking

A Kotlin networking library built on top of Ktor Sockets / WebSockets, featuring a flexible transformer pipeline and
built-in encryption.


## Supported Platforms
- JVM, Android
- JS, WasmJS
- Native (Linux, macOS, tvOS, iOS, watchOS, Windows)


## Modules

| Module                         | Purpose                                                               |
|--------------------------------|-----------------------------------------------------------------------|
| `networkink-core`              | Core API                                                              |
| `networkink-protocol`          | `PacketType`, `PacketEncoder` and `PacketDecoder` transformers        |
| `networkink-sockets`           | `SocketConnection` based on `ktor.network` (TCP)                      |
| `networkink-sockets-server`    | `SocketServer`: TCP server                                            |
| `networkink-sockets-client`    | `connect(host, port)`: TCP client                                     |
| `networkink-websockets`        | `WebSocketConnection` based on `ktor.websocket` (WebSocket)           |
| `networkink-websockets-server` | `WebSocketServer`: WebSocket server                                   |
| `networkink-websockets-client` | `connect(url)`: WebSocket client                                      |
| `networkink-test`              | Abstract `NetworkingTest` class reused by socket and WebSocket tests. |


## Usage examples

### Client (Sockets)

```kotlin
val connection = connect("localhost", 8080)
connection.applyHandler(ClientHelloHandler)

connection.incomingTransformers += BytesToBitsTransformer
connection.incomingTransformers += PacketDecoder(packetTypes)
connection.outgoingTransformers += PacketEncoder(packetTypes)
connection.outgoingTransformers += BitsToBytesTransformer

connection.send(MyPacket("Hello!"))
```

### Client (WebSockets)

```kotlin
val connection = connect("ws://localhost:8080")
// ...
```

### Server (Sockets)

```kotlin
val server = SocketServer(port = 8080)
server.start()
server.acceptConnections { connection ->
    connection.applyHandler(ServerHelloHandler)
    connection.incomingTransformers += BytesToBitsTransformer
    connection.incomingTransformers += PacketDecoder(packetTypes)
    connection.outgoingTransformers += PacketEncoder(packetTypes)
    connection.outgoingTransformers += BitsToBytesTransformer
    connection.applyHandler(MyPacketHandler)
    connection.receiveLoop()
}

object MyPacketHandler : NetworkHandler<MyPacket>(MyPacket::class) {
    override suspend fun handle(connection: Connection, packet: MyPacket) {
        println("Received from client: ${packet.message}")
    }
}
```

### Server (WebSockets)

```kotlin
val server = WebSocketServer(ktorEngine = Netty, port = 8080)
server.start()
server.acceptConnections { connection ->
    // ...
}
```

### Combined server

```kotlin
val socketServer = SocketServer(port = 8080)
val webSocketServer = WebSocketServer(ktorEngine = Netty, port = 8081)
val server = CombinedServer(setOf(socketServer, webSocketServer))
server.start()
// ...
```

### Defining a packet type

```kotlin
val packetTypes = listOf(MyPacket.Type)

class MyPacket(val message: String) {
    object Type : PacketType<MyPacket>("my_packet", MyPacket::class) {
        override fun encode(buffer: Buffer, packet: MyPacket) {
            buffer.writeString(packet.message)
        }

        override fun decode(buffer: Buffer): MyPacket {
            return MyPacket(buffer.readString())
        }
    }
}
```