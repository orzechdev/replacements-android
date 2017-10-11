# replacements-android

#### Architektura aplikacji

Aplikacja jest oparta o Komponenty Architektury Google tj.:
https://developer.android.com/topic/libraries/architecture/index.html

![alt text](https://github.com/orzechdev/replacements-android/blob/beta/README_FILES/final-architecture.png)

#### Uruchamianie aktywności przez konsole

Aby uruchomić Activity nowej wersji aplikacji w konsoli z folderu sdk z adb.exe wpisz:

    adb shell

a następnie:

    am start -n com.studytor.app/.activities.ActivityMain

#### Klasa POJO dla JSON

Do tworzenia obiektów POJO na podstawie pliku JSON można użyć (wybierając Source type: JSON, Annotation style: Gson):
http://www.jsonschema2pojo.org/

#### Logi

Do tworzenia logów na potrzebę debugowania najlepiej wypisywać po kolei skąd log jest wywoływany tzn. klasa -> metoda -> funkcja -> numer w setkach (100, 200, 300, ...).
Nazwę klasy można otrzymać ze zmiennaj (najlepiej ją dodać na początku klasy):

    private static final String CLASS_NAME = ReplacementsMain.class.getName();