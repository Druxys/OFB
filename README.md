﻿# OFB


## Exemple d'utilisation


Chiffrement: `java -jar OFB_Paul.jar -e 7 10101010 .\Test.txt`

Dechiffrement: `java -jar OFB_Paul.jar -d 7 10101010 .\test.encrypted`

## Commandes

### 1er argument :

`-e` pour le chiffrement

`-d` pour le déchiffrement

### 2eme argument :

Nombre de bits de la clé

*exemple :*`7`

### 3eme argument :

La clé de chiffrement en binaire (plus grande que la longueur de la clé)

*exemple :*`10101010`

### 4eme argument :

Le fichier à chiffrer ou déchiffrer

*exemple :*`./Test.txt`
