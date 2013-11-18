SET ECLIPSE="C:\Program Files\pleiades-e3.6-platform-jre_20100623\eclipse\eclipse.exe"
IF EXIST %ECLIPSE% (%ECLIPSE% -data . ; exit)
SET ECLIPSE="C:\Program Files (x86)\pleiades-e3.6-ultimate-jre_20101005\eclipse\eclipse.exe"
IF EXIST %ECLIPSE% (%ECLIPSE% -data . ; exit)
SET ECLIPSE="C:\Program Files\pleiades-e3.6-platform-jre_20101025\eclipse\eclipse.exe"
IF EXIST %ECLIPSE% (%ECLIPSE% -data . ; exit)
