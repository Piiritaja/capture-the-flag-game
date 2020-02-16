# Dokumentatsioon

## Tehnoloogia
*  [GLFW](https://www.glfw.org)
*  [OpenGl](https://www.opengl.org)
*  [lwjgl](https://www.lwjgl.org)
*  [OpenCL](https://www.khronos.org/opencl/)
*  [OpenAL](http://www.openal.org)


### Docs

lwjgl teegiga on juba kõik ellnevad kaassa. Dokumentatsiooniga oleks ikka vaja tutvuda.

[GLFW](https://www.glfw.org/documentation.html)

[OpenGL](https://www.opengl.org/documentation/)

[OpenCl](https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/)

[OpenAl](http://www.openal.org/documentation/)


### lwjgl version


[lwjgl 3.2.3](https://www.lwjgl.org/customize)


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

Mänguvälja konspekt, kus 3 tiimi, botid mänguväljal, igas tiimis 4 liiget ja takistused mänguväljal.

[Imgur](https://i.imgur.com/HY8iyZR.png)