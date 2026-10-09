#define MyAppName "Rabidanti"
#define MyAppVersion "0.1.0"
#define MyAppPublisher "Rabidanti"
#define MyAppExeName "Rabidanti.exe"

[Setup]
AppId={{A5F0E7D1-9C3A-4B6E-8D2A-5F0C7B9E1A44}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\Rabidanti
DefaultGroupName=Rabidanti
OutputDir=dist
OutputBaseFilename=Rabidanti-Windows-Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern
ArchitecturesInstallIn64BitMode=x64
PrivilegesRequired=lowest
UninstallDisplayIcon={app}\{#MyAppExeName}

[Files]
Source: "dist\Rabidanti.exe"; DestDir: "{app}"; Flags: ignoreversion

[Tasks]
Name: "desktopicon"; Description: "Criar atalho no ambiente de trabalho"; GroupDescription: "Atalhos:"

[Icons]
Name: "{group}\Rabidanti"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\Rabidanti"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Abrir Rabidanti"; Flags: postinstall nowait skipifsilent
