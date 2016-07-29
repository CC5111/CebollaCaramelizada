**Administrador y descargador de Series**
================================

**Descripción del Proyecto**
-----------------------------------
Se propone realizar una aplicación web para buscar, almacenar y descargar capítulos de series apenas se encuentren disponibles en Internet, con subtítulos en el idioma escogido por el usuario.

**Back-End**
La aplicación tendrá un back-end que se encargará de realizar diversas consultas sobre series, usando la API de trakt. Entre las consultas a realizar están:

* Información general sobre una serie.
* Idioma de la serie.
* Temporadas disponibles de una serie.
* Capítulos disponibles en una temporada.
* Fecha de lanzamiento de próximo capítulo de una serie.
* Estado de una serie (finalizado, en emisión).
* Series populares.

El back-end se encargará de mantener una biblioteca con las series que sean añadidas por el usuario.

Se usará la tecnología **BitTorrent** para descargar los capítulos, usando la API de **torrentproject**. Para buscar los subtítulos, se usará la API de **opensubtitles**.

[comment]: <> (Estas librerías podrían cambiar luego de una investigación mas profunda, es decir podrían ser no usadas o reemplezadas por una equivalente)

La aplicación será capaz de ofrecer una lista de opciones de descarga para un cierto capítulo que obtendrá de una "fuente", además para cada serie será capaz de ofrecer una lista de subtítulos correspondientes.

La aplicación será capaz de descargar automáticamente capítulos ya emitidos como no emitidos de una serie en la biblioteca. Para el último caso la aplicación descargará a penas salga el capítulo.

La aplicación será capaz de detectar si hay espacio suficiente en disco para concluir una descarga.

**Front-End**
El front-end de la aplicación será web, y en él se podrá:

* Realizar una búsqueda de series.
* Agregar una serie a la biblioteca.
* Configurar el directorio de descarga de las series.
* Configurar la descarga automática de nuevos capítulos.
* Descargar una serie completa.
* Descargar una temporada completa.
* Descargar un capítulo.
* Administrar la biblioteca de series.
* Es deseable contar con un reproductor incorporado que permita reproducir los capítulos descargados.

Tanto el front-end como el back-end serán programados en Scala usando los frameworks **Play** y **Akka**.

[comment]: <> (Podrían ser agregados otros frameworks en el futuro.)
