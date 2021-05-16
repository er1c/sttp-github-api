name := "sttp-github-api"
scalaVersion := "2.13.5"
version := "0.1.0"

// https://raw.githubusercontent.com/github/rest-api-description/main/descriptions/api.github.com/api.github.com.yaml

lazy val githubApi: Project = project
  .in(file("github-api"))
  .settings(
    openApiInputSpec := s"${baseDirectory.value.getPath}/api.github.com.yaml",
    openApiGeneratorName := "scala-sttp",
    //We can't use src_managed because there is no option to tell openapi-generator to generate files into different folder than src
    //see https://github.com/OpenAPITools/openapi-generator/issues/6685 for more details
    openApiOutputDir := baseDirectory.value.name,
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.3.3",
      "com.softwaremill.sttp.client3" %% "json4s" % "3.3.3",
      "org.json4s" %% "json4s-jackson" % "3.6.8"
    ),
    //We can't use sourceGenerators because this requires all files to compile and openapi-generator generates
    //some additional metadata files which breaks compilation.
    //see https://github.com/OpenAPITools/openapi-generator/issues/6685 for more details
    (compile in Compile) := ((compile in Compile) dependsOn openApiGenerate).value,
    //As we don't generate files into src_managed we have to do cleaning by our own
    cleanFiles += baseDirectory.value / "src"
  )

  lazy val core = project
    .in(file("core"))
    .dependsOn(githubApi)

  lazy val rootProject = project
    .in(file("."))
    .aggregate(githubApi, core)
