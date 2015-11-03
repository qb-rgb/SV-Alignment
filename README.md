Quentin Baert  
M2 MOCAD  

# Réalisation d'un read mapper

## Objectifs

L'objectif de ce TP était de réaliser un read mapper, c'est à dire un logiciel qui aligne des reads sur un génome.

## Récupération

Les sources du TP sont disponibles à l'adresse suivante :
```
https://github.com/qw0te/SV-Alignment.git
```

## Architecture

```
src
└── main
    └── scala
        ├── align
        │   ├── Aligner.scala
        │   ├── Alignment.scala
        │   ├── Main.scala
        │   ├── SemiGlobalAligner.scala
        │   └── align.scala
        ├── extension
        │   ├── Extender.scala
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

Avec :
* `align` qui contient les sources pour l'alignement de deux séquences,
* `indexation` qui contient les sources pour la phase d'indexation d'un read mapper,
* `search` qui contient les sources pour la phase de recherche d'un read mapper,
* `extension` qui contient le sources pour la phase d'extension d'un read mapper,
* `fasta` qui contient les sources pour lire les fichiers FASTA et FASTQ.

## Compilation

Le projet est réalisé en Scala à l'aide de l'outil *Scala Build Tool* (ou SBT disponible ici http://www.scala-sbt.org/download.html).  
Pour compiler et avoir le `jar` à la racine du projet, exécuter les commande suivante depuis la racine :
```
sbt assembly
mv target/scala-2.10/aligner.jar .
```

Un `jar` exécutable est également fourni dans l'archive du TP.

## Utilisation

Pour utiliser le `jar`, utilser la commande suivante :
```
java -jar aligner.jar -genome [pathG] -reads [pathR] -seedlength [s] -seednb [max|min] -match [m] -mismatch [mm] -indel [i] -purcent [p]
```

Avec :
* `[pathG]` chemin vers le fichier FASTA qui contient le génome,
* `[pathR]` chemin vers le fichier FASTQ qui contient les reads,
* `[s]` taille des seeds extraites des reads,
* `[max|min]` `max` pour avoir le nombre maximal de seeds par read, `min` pour en avoir le nombre minimal,
* `[m]` score des matchs lors de l'alignement,
* `[mm]` score des mismatchs lors de l'alignement,
* `[i]` score des indels lors de l'alignement
* `[p]` pourcentage d'erreur toléré sur l'alignement d'un read avec le génome

L'ordre des paramètre n'importe pas.

Exemple d'utilisation :
```
java -jar aligner.jar -genome NC_002549.fna -reads SRR1930021.fastq -seedlength 25 -match 5 -mismatch -4 -indel -10 -purcent 10 -seednb min
```

## Réalisations

* L'alignement de deux séquences peut être optimisé grâce à une programmation dynamique de type *k-band*.

* Il est possible de demander la matrice d'alignement à un `Aligner`.

* Une seule méthode d'indéxation a été implémentée : la table des suffixes.

* Deux méthodes d'extraction de seed à partir d'un read ont été implémentées :
  * la première permet d'extraire le minimum de seeds d'un read tout en le couvrant totalement,
  * la seconde permet d'extraire le maximum de seeds d'un read (soit un écart d'un seul nucléotide entre deux seeds successives dans le read).


* Une seule méthode de recherche des graines a été implémentée : la recherche exacte.

* Les alignements sont affichés de manière claire est lisible :
  * le génome se trouve toujours en haut (et donc le read en dessous),
  * les deux séquences sont affichés par ligne de 50 caractères,
  * les deux séquences sont indéxées afin que l'on puisse les retrouver dans le génome et/ou le read concerné,
  * les matchs, mismatchs et indel sont clairement indiqués.


* Lors de l'alignement, les reads qui sont suffisamment bien aligner avec le read sont imprimés.

* Un dossier test est fourni avec à l'intérieur un petit génome de test ainsi que quelques reads. Ce petit jeu de tests montre bien que les reads sans ressemblance avec le génome ne trouvent pas d'alignement, que ceux directement extraits du génome trouve un alignement parfait (les tests incluent des reads aux positions limites du génome), et que les reads quelques peu modifiés trouvent tout de même un alignement avec des nucléotides qui ne matchent pas.

## Résultats

### Résultats sur l'alignement de test

Un alignement de test à été fourni en début de TP.

L'alignement obtenu est le même à une position d'indel prêt, mais que ne change rien aux caratctéristiques de l'alignement (nombre de matchs, nombres de gaps, score).

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
      1 tgggatggatcaaccctaacagtggtggcacaaacta-tgcacagaagtt
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

### Résultats sur Ebola

Les tests sur le génome d'Ebola donnent les résulats suivants :

* Avec le minimum de seeds par read, les 3389 reads sont traités en 30 secondes et 76% sont correctement alignés.
```
76% of aligned reads
28.40 real        31.43 user         1.14 sys
```

* Avec le maximum de seeds par read, les 3389 reads sont traités en 576 secondes (soit environ 9,5 minutes).
```
569.54 real       576.20 user        19.98 sys
```

### Résultats sur le génome humain

Aucun test sur le génome humain n'a été réalisé.
