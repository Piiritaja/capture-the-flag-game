# Dokumentatsioon

## Tehnoloogia
*  [JavaFX](https://openjfx.io)
*  Css
*  FXML


### Docs


[Javafx](https://openjfx.io/javadoc/13/)




### Server

[Kryonet](https://github.com/EsotericSoftware/kryonet)

TCP server

## Mängu loogika

* kaks või kolm tiimi
* Tiimid alustavad baasist, mis on mapil märgitud
* Punkti skoorib see tiim, kes esimesena vastase lipu oma baasi viib
* Kui üks tiim skoorib, algab järgmine round.
* Igal mängijal on relv, millega saab vastasmängija maha lasta
* friendly fire väljas
* Kui mängija sureb samal ajal kui lippu käes, jääb lipp sinna samasse kohta maha
* Ükskõik millist mänguväljal olevat lippu (vastase või enda oma) saab iga mängija üles võtta
* Oma tiimi lipu saab enda baasi tagasi viia (Kui see juba vastase baasis pole)
* Mänguväljal on botid

## Botid

* Lippe ei saa üles võtta
* Igal botil on relv
* Erapooletud (tulistavad iga tiimi mängijat kes ette jäävad)
* Ei liigu mapil "vabalt ringi" (Kindel raadius, mille sees on võimelised liikuma) 

## Mänguväli

Mänguvälja konspekt, kus 3 tiimi, botid mänguväljal(kastid), igas tiimis 4 liiget ja takistused mänguväljal.

![alt text](https://i.imgur.com/HY8iyZR.png)