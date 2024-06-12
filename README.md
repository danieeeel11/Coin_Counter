# Coin Counter

Este repositorio contiene la implementación de una aplicación de Android que detecta las monedas en una superficie y cuenta el valor total de las mismas. Utilizando técnicas de procesamiento de imágenes, la aplicación identifica y suma el valor de cada moneda detectada.

## Contenido

1. [Abstract](#abstract)
2. [Tecnologías Utilizadas](#Tecnologías_Utilizadas)
3. [Implementación](#Implementación)
4. [Referencias](#Referencias)

## Abstract
La detección y conteo de monedas es un desafío que se puede abordar eficazmente mediante técnicas de procesamiento de imágenes. Esta aplicación de Android emplea métodos como el método de Hough para identificar y contar monedas de COP en una superficie, proporcionando una estimación del valor total. La implementación incluye el uso del método de Hough para la detección y clasificación de monedas mediante la detección de circulos.

## Tecnologías_Utilizadas
* Java/Kotlin: Lenguaje de programación principal para la implementación de la aplicación.
* OpenCV: Biblioteca de procesamiento de imágenes utilizada para la detección de monedas.
* Android Studio: Entorno de desarrollo integrado (IDE) para la construcción de la aplicación.

## Implementación
La implementación del proyecto se divide en varias etapas:
* Captura de Imagen: Uso de la cámara del dispositivo para capturar imágenes de las monedas.
* Preprocesamiento de Imagen: Aplicación de técnicas de procesamiento de imágenes para mejorar la calidad de la detección.
* Detección y Clasificación de Monedas: Uso del método de Hough para identificar y clasificar las monedas según su valor.
* Cálculo del Valor Total: Suma del valor de las monedas detectadas para proporcionar un total.

## Referencias
OpenCV Documentation: https://docs.opencv.org/
