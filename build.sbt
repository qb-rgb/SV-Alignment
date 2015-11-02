name := "SV-Alignement"

version := "1.0"

scalacOptions += "-deprecation"

assemblyJarName in assembly := "aligner.jar"

mainClass in assembly := Some("align.Main")
