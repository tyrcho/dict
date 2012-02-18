set CP=dictionary.jar
for %%i in (lib\*) do call addtocp %%i
 java -cp %CP% com.tyrcho.dictionary.view.DictionaryFrame D:\documents\arabic.txt d:\documents\dictionnaire.jrxml

