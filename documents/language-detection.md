# OpenNLP language detection model

- Android model dependency: `org.apache.opennlp:opennlp-models-langdetect:1.3.0` from Maven Central.
- Upstream packaging: [Apache OpenNLP Models 1.3.0](https://github.com/apache/opennlp-models/blob/opennlp-models-1.3.0/opennlp-models-langdetect/pom.xml).
- Model file: `langdetect-183.bin`, model version `1.8.3`. The model package and OpenNLP Tools have independent versions.
- SHA-256: `2ddf585fac2e02a9dcfb9a4a9cc9417562eaac351be2efb506a2eaa87f19e9d4`
- The dependency JAR includes Apache LICENSE and NOTICE files. The model archive also includes its license, notice and documentation.
- Gradle packages the model from the dependency as an Android Java resource, loaded with `getResourceAsStream("/langdetect-183.bin")` without runtime downloads. Do not commit a separate copy of the model to this repository.
- OpenNLP Tools 2.1.0 is used to match the project's Java 11 target (2.3.3 requires Java 17).

The model detects 103 languages and works best with at least two sentences in the same language.
Predictions without an ISO 639-1 equivalent return no suggestion.
