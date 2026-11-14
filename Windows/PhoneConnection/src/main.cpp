
#include <winsock2.h>
#include <ws2tcpip.h>
#include <iostream>

const int PORT = 5001;
const char* RESPONSE = "PHONE2PC_STOP-YELLING";

int main(int argc, char* argv[]) {
	if (argc != 2) return 1;

    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);

    SOCKET clientSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_port = htons(PORT);
    if (inet_pton(AF_INET, argv[1], &address.sin_addr) != 1) {
        std::cerr << "Invalid IP address\n";
		return 1;
    }
    
    if (connect(clientSocket, (sockaddr*)&address, sizeof(address)) == SOCKET_ERROR) {
        std::cout << "Connection failed: " << WSAGetLastError() << '\n';
        return 1;
    }
    else {
        std::cout << "Connected\n";
    }

    if (send(clientSocket, RESPONSE, strlen(RESPONSE), 0) == SOCKET_ERROR) {
		std::cerr << "Send failed\n";
		return 1;
    }

    while (true) {}

    closesocket(clientSocket);
    WSACleanup();

    return 0;
}