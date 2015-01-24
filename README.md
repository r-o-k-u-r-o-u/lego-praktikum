# Lego Praktikum
Team 6





###GIT

####1. Download
```
http://git-scm.com/downloads
```

####2. Repo klonen
``` 
git clone https://github.com/r-o-k-u-r-o-u/lego-praktikum.git
```

####3. Konfigurieren

Damit man später die commits eurem Account zugeordnet werden zumindest
die E-Mail adresse auf die des GIT-Accounts setzen.
```
git config --global user.name "<name>"
git config --global user.email <mail>
```

####4. Commiten
```
git commit -am "<text_über_änderungen>"
```
oder
```
git commit -a
```
dann könnt ihr über den Editor `git config core.editor` einen Text eingeben.
####5. Änderungen hochladen
```
git push origin master
```