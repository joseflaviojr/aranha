mkdir c:\Aranha
xcopy /s /y Aranha\* c:\Aranha
start Ferramentas\winpcap.exe
copy Ferramentas\windump.exe c:\Aranha
copy "Aranha\Aranha 2011.lnk" "%USERPROFILE%\Desktop"