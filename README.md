Quentin Baert  
Master MOCAD

# Réalisation d'un read mapper

## Objectif

L'objectif de ce TP était de réaliser un read mapper, c'est à dire une application qui aligne des reads avec un bout de génome.

## Récupération

Le TP se trouve à l'adresse suivante :
```
https://github.com/qw0te/SV-Alignment
```

## Architecture

```
src
└── main
    └── scala
        ├── align
        │   ├── Aligner.scala
        │   ├── Alignment.scala
        │   ├── SemiGlobalAligner.scala
        │   └── align.scala
        ├── extension
        │   ├── MaximalSeedExtracter.scala
        │   ├── MinimalSeedExtracter.scala
        │   ├── SeedExtracter.scala
        │   └── extension.scala
        ├── fasta
        │   ├── FASTAReader.scala
        │   ├── FASTQReader.scala
        │   └── fasta.scala
        ├── indexation
        │   ├── Indexer.scala
        │   ├── SuffixTableIndexer.scala
        │   └── indexation.scala
        └── search
            ├── ExactSeeker.scala
            ├── Seeker.scala
            └── search.scala
```

## Compilation

Le TP a été réalisé à l'aide de l'outil *Scala Build Tool* (disponible ici http://www.scala-sbt.org/download.html).  
Pour compiler et obtenir le `jar` à la racine du projet exécuter :
```
sbt assembly
mv target/scala-2.10/aligner.jar
```

## Exécution

Pour exécuter le `jar` exécuter la commande suivante :
```
java -jar aligner.jar -genome [pathG] -reads [pathR] -seedlength [s] -seednb [max|min] -match [m] -mismatch [mm] -indel [i] -purcent [p]
```

Avec :
* `[pathG]` chemin vers le fichier FASTA contenant le génome
* `[pathR]` chemin vers le fichier FASTQ contenant les reads
* `[s]` taille des seeds à extraire des reads
* `[max|min]` `max` pour extraire le maximum de seed d'un read, `min` pour en extraire le minimum
* `[m]` score des matchs lors de l'alignement
* `[mm]` score des mismatchs lors de l'alignement
* `[i]` score des indel lors de l'alignement
* `[p]` pourcentage d'erreur toléré lors de l'alignement d'un read sur le génome

L'ordre des paramètres n'importe pas.

Exemple d'utilisation :
```
java -jar aligner.jar -genome NC_002549.fna -reads SRR1930021.fastq -seedlength 25 -match 5 -mismatch -4 -indel -10 -purcent 10 -seednb min
```

## Réalisation

* Il est possible d'améliorer la programmation dynamique d'un alignement grâce à un *k-band*

* Les alignement sont présenté de manière claire :
  * Le génome au dessus, le read en dessous
  * Les séquence sont divisés en lignes de 50 nucléotides
  * Chaque séquence est indicé pour pouvoir la retrouver par la suite dans la séquence d'origine
  * Les matchs, mismatchs, indels sont clairement indiqués

* Une seule technique d'indexation a été implémentée : la table des suffixes

* Deux méthodes d'extraction de seeds ont été implémentées
  * La première permet d'extraire le nombre minimal de seeds d'un read tout en le couvrant totallement
  * La seconde permet d'extraire le nombre maximam de seeds d'un read

* Une seule technique de recherche de seed dans le génome a été implémenté : la recherche exacte

* Lors d'un alignement, si un read match suffisamment avec le génome, l'alignement est imprimé sur la sortie standard

* Les génomes sont extraits de fichier FASTA, les reads de fichiers FASTQ

* Un dossier `test` contient un court genome ainsi que quelques reads. Ces reads correspondent à des situations bien différentes :
  * Les reads issus directement du génome sont parfaitement alignés
  * Les reads qui s'alignent aux position limites du génome s'alignent correctement également
  * Les reads aléatoire ne correspondent pas assez avec le génome, leur alignement ne sont pas renvoyés
  * Les reads provenant du génome mais légèrement modifiés sont également aligné avec quelques erreurs

## Résultats

### Sur l'alignement de test

Un alignement test a été fourni au début du TP. Ce dernier est correctement aligné.

```
scala> import align._
import align._

scala> val s1 = "tgggatggatcaaccctaacagtggtggcacaaactatgcacagaagtttcagggcagggtcaccatgaccagggacacgtccatcagcacagcctacatggagctgagcaggctgagatctgacgacacggccgtgtattactgtgcgagaga"
s1: String = tgggatggatcaaccctaacagtggtggcacaaactatgcacagaagtttcagggcagggtcaccatgaccagggacacgtccatcagcacagcctacatggagctgagcaggctgagatctgacgacacggccgtgtattactgtgcgagaga

scala> val s2 = "ttgcacgcattgattgggatgatgataaatactacagcacatctctgaagaccaggctcaccatctccaaggacacctccaaaaaccaggtggtccttacaatgaccaacatggaccctgtggacacggccgtgtattactg"
s2: String = ttgcacgcattgattgggatgatgataaatactacagcacatctctgaagaccaggctcaccatctccaaggacacctccaaaaaccaggtggtccttacaatgaccaacatggaccctgtggacacggccgtgtattactg

scala> val aligner = new SemiGlobalAligner(0, s1, s2, 5, -4, -10, 9)
aligner: align.SemiGlobalAligner = align.SemiGlobalAligner@71bab54a

scala> aligner.alignment
res0: align.Alignment =
      2 tgggatggatcaaccctaacagtggtggcacaaacta-tgcacagaagtt
        | | | | ||  |   |   | || ||  | | ||||  |||||     |
      1 ttgcacgcattga--ttggga-tgatgataaatactacagcaca-tctct
     51 tcagggcagggtcaccatgaccagggacacgtccatcagcaca-g-ccta
          ||  |||| |||||||  ||| |||||| ||||  | | || |   |
     47 gaagaccaggctcaccatctccaaggacacctccaaaaac-caggtggtc
     99 catggagctgagcaggctgaga-tctgacgacacggccgtgtattactgt
        | |  |  ||| ||   || ||  |||  ||||||||||||||||||||
     96 cttaca-atgaccaacatg-gaccctgtggacacggccgtgtattactg-
    148 gcgagaga

    142 --------


scala> aligner.alignment.countMatches
res1: Int = 92

scala> aligner.alignment.countGaps
res2: Int = 20
```

### Sur Ebola

Deux tests différents ont été fait sur le génome et les reads d'Ebola.

* Avec un nombre minimal de seeds extraites par read, le temps d'exécution est de  secondes.

* Avec un nombre maximal de seeds extraites par read, le temps d'exécution est de 9,5 minutes.

Environ 76% des reads sont correctements alignés.

### Sur le génome humain

Aucun tests n'a été réalisé sur le génome humain.
