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
