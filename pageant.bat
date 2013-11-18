IF NOT "%ProgramFiles(x86)%" == "" SET ProgramFiles=C:\Program Files (x86)
FOR %%a IN (C:\cygwin\home\%USERNAME%\.ssh\*.ppk) DO "%ProgramFiles%\PuTTY\pageant.exe" "%%a"

FOR %%a IN ("%USERPROFILE%\My Documents\*.ppk") DO "%ProgramFiles%\PuTTY\pageant.exe" "%%a"
FOR %%a IN ("%USERPROFILE%\Documents\*.ppk") DO "%ProgramFiles%\PuTTY\pageant.exe" "%%a"

PAUSE
