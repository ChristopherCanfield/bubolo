#include <Windows.h>
#include <processthreadsapi.h>
#include <errhandlingapi.h>

#include <string>
#include <cstdio>

using std::wstring;
using std::printf;

int main(int argCount, char* args[]) {
	STARTUPINFO startupInfo;
	ZeroMemory(&startupInfo, sizeof(startupInfo));

	PROCESS_INFORMATION processInfo;
	ZeroMemory(&processInfo, sizeof(processInfo));

	wstring commandLineParams(L"-ea -cp \"Bubolo.jar\" bubolo.Main");

	if (!CreateProcess(L"jdk-16.0.0.36-hotspot\\bin\\java.exe", // Path to process.
		&commandLineParams[0], // Command line parameters.
		nullptr,
		nullptr,
		FALSE,
		0,
		nullptr,
		nullptr, // By setting to null, we are instructing the child process to use this process's starting directory.
		&startupInfo,
		&processInfo)) {

		printf("Unable to launch Bubolo: %d", GetLastError());
		return -1;
	}

	WaitForSingleObject(processInfo.hProcess, INFINITE);
	CloseHandle(processInfo.hProcess);
	CloseHandle(processInfo.hThread);
}