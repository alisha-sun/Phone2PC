#include <winsock2.h>
#include <iostream>
#include <vector>

#pragma comment(lib, "Ws2_32.lib")

int main() {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);

    SOCKET server = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_port = htons(5000);
    address.sin_addr.s_addr = INADDR_ANY;

    bind(server, (sockaddr*)&address, sizeof(address));
    listen(server, 1);

    std::cout << "Waiting for connection...\n";

    SOCKET client = accept(server, nullptr, nullptr);

    std::cout << "Connected!\n";

    char buffer[4096];
    while (true) {
        int received = recv(client, buffer, sizeof(buffer), 0);

        if (received <= 0)
            break;
        std::cout << "Received " << received << " bytes\n";
    }

    closesocket(client);
    closesocket(server);
    WSACleanup();

    while (true) {}

    return 0;
}