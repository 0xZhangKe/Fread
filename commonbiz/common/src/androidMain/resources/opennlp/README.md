# OpenNLP language detection model

- Source: https://downloads.apache.org/opennlp/models/langdetect/1.8.3/langdetect-183.bin
- SHA-256: `2ddf585fac2e02a9dcfb9a4a9cc9417562eaac351be2efb506a2eaa87f19e9d4`
- The original archive includes LICENSE.txt, NOTICE.txt and model documentation; keep it intact.
- Bundled as an Android Java resource, loaded locally without runtime downloads.
- OpenNLP Tools 2.1.0 is used to match the project's Java 11 target (2.3.3 requires Java 17).

The model detects 103 languages and works best with at least two sentences in the same language.
Predictions without an ISO 639-1 equivalent return no suggestion.
